# OrbitaSafe — Modelagem DDD em Java

Implementação em **Domain-Driven Design** do sistema **OrbitaSafe**, a solução que o grupo está desenvolvendo ao longo de todas as matérias da Global Solution 2026 (FIAP). O OrbitaSafe monitora eventos climáticos extremos por meio de satélites com sensores embarcados e emite alertas para áreas afetadas. Esta entrega é a fatia Java do projeto, focada em demonstrar o domínio segundo os padrões de DDD.

> **Persistência:** todo o projeto trabalha com **dados mockados** em memória (implementações `InMemory*Repository`). Nenhuma integração com banco real é necessária para rodar — basta executar o `Main`.

---

## 1. Como executar

Requer **JDK 17+** (testado em OpenJDK 25).

```bash
# A partir da pasta java/
javac -d out -encoding UTF-8 $(find src/main/java -name "*.java")
java -cp out -Dfile.encoding=UTF-8 br.com.fiap.orbitasafe.Main
```

Em IntelliJ: abra a pasta `java/` como projeto, marque `src/main/java` como *Sources Root* (já configurado em `.idea/java.iml`) e execute `Main.java`.

A demonstração:

1. Cadastra regiões monitoradas
2. Lança satélite + instala 3 sensores
3. Mostra invariante do agregado bloqueando sensor duplicado
4. Detecta tempestade severa → emite alerta CRÍTICO automaticamente
5. Detecta seca moderada → emite alerta MODERADO
6. Lista todos os Domain Events publicados

---

## 2. Caminho dos dados (como testar o projeto)

Como tudo é mockado em memória, o teste é o próprio `Main.java`. O fluxo abaixo mostra por **quais camadas DDD** cada dado passa — útil para entender, depurar e estender o projeto.

```
 Main (chamador / camada de apresentação)
   │
   │  chama caso de uso
   ▼
 Application Service                  ← orquestra, sem regras de negócio
   │
   │  carrega/salva agregados
   ▼
 Repository (interface no domínio)
   │
   │  resolvida em runtime por…
   ▼
 InMemory*Repository (infrastructure) ← HashMap mockado
   │
   │  devolve Aggregate Root
   ▼
 Aggregate Root + Entities + VOs      ← aqui vivem as regras / invariantes
   │
   │  publica Domain Events
   ▼
 InMemoryDomainEventPublisher         ← entrega para subscribers (logs, etc.)
```

### Trajeto concreto de um evento climático

Quando o `Main` chama `monitoramento.detectarEvento(TEMPESTADE, litoralSP, orbiSat1, leituras)`:

1. **`MonitoramentoClimaticoApplicationService.detectarEvento(...)`** valida que região e satélite existem nos repositórios mockados.
2. Delega para o **Domain Service `ServicoDeteccaoEvento`**, que:
   - confere que cada `SensorId` da leitura pertence ao satélite,
   - calcula a `Intensidade` agregada com fator de ajuste por `TipoEvento`,
   - cria o agregado `EventoClimatico` via factory `detectar(...)`,
   - registra cada leitura como `Observacao` interna do agregado.
3. Delega para o **Domain Service `PoliticaEmissaoAlerta`**, que examina a intensidade e, se for o caso, chama `EventoClimatico.emitirAlerta(...)` (gerando uma entity `Alerta` interna).
4. O agregado acumula `EventoClimaticoDetectadoEvent`, `ObservacaoRegistradaEvent` e `AlertaEmitidoEvent` em sua lista de eventos pendentes.
5. O Application Service salva o agregado no `InMemoryEventoClimaticoRepository` e chama `publisher.publicarPendentes(evento)` — só então os eventos são entregues aos subscribers cadastrados no `Main`.

### Pontos onde plugar testes manuais

Tudo pode ser testado editando `Main.java`:

| Para testar…                                | Onde mexer                                                                 |
| ------------------------------------------- | -------------------------------------------------------------------------- |
| Validação de VO (ex: latitude inválida)     | Mudar coordenadas em `regiaoService.cadastrarRegiao(...)`                  |
| Invariantes do `Satelite`                   | Tentar instalar dois sensores do mesmo tipo, ativar sem sensores etc.      |
| Cálculo do `ServicoDeteccaoEvento`          | Trocar valores das `LeituraSensor` ou o `TipoEvento`                       |
| Política de alerta                          | Variar a intensidade média das leituras (≥30 MODERADO, ≥60 ALTO, ≥80 CRÍTICO) |
| Publicação de Domain Events                 | Adicionar novos `publisher.assinar(Evento.class, ...)` no `Main`           |

---

## 3. Bounded Contexts

```
+-------------------------+        +--------------------------+
| Monitoramento Espacial  |        | Eventos Climaticos       |
|-------------------------|        |--------------------------|
| Satelite (AR)           |        | EventoClimatico (AR)     |
|   - Sensor (Entity)     |  ----> |   - Observacao (Entity)  |
|   - StatusOperacional   | (id)   |   - Alerta (Entity)      |
|   - TipoSensor          |        |   - Intensidade (VO)     |
|   - Precisao (VO)       |        | ServicoDeteccaoEvento    |
+-------------------------+        | PoliticaEmissaoAlerta    |
                                   +-------------+------------+
                                                 | (id RegiaoId)
                                                 v
                                   +--------------------------+
                                   | Geografia                |
                                   |--------------------------|
                                   | Regiao (AR)              |
                                   |   - Coordenadas (VO)     |
                                   |   - Pais (VO)            |
                                   |   - NomeRegiao (VO)      |
                                   +--------------------------+
```

### Context Map

| De                       | Para                     | Padrão de integração          | Detalhe                                                     |
| ------------------------ | ------------------------ | ----------------------------- | ----------------------------------------------------------- |
| Eventos Climáticos       | Monitoramento Espacial   | **Customer / Supplier**       | Eventos consomem `SensorId` para rastrear origem da medição |
| Eventos Climáticos       | Geografia                | **Conformist** via `RegiaoId` | Evento referencia região por ID                             |
| Monitoramento Espacial   | Geografia                | **Shared Kernel**             | Ambos compartilham `Pais` (VO de identidade nacional)       |

O agregado `EventoClimatico` nunca navega para `Regiao` ou para `Satelite` por referência direta — apenas guarda os IDs (`RegiaoId`, `SensorId`). É a regra DDD de **independência entre agregados**.

---

## 4. Estrutura de pacotes

```
br.com.fiap.orbitasafe
├── shared.domain                 -- AggregateRoot, Entity, DomainEvent, Repository (kernel)
├── geografia
│   ├── domain.model              -- Regiao (AR), NomeRegiao, Pais, Coordenadas (VOs)
│   ├── domain.repository         -- RegiaoRepository
│   └── application               -- RegiaoApplicationService
├── monitoramentoespacial
│   ├── domain.model.satelite     -- Satelite (AR), NomeSatelite, DataLancamento, StatusOperacional
│   ├── domain.model.sensor       -- Sensor (Entity), TipoSensor, Precisao
│   ├── domain.event              -- SateliteLancadoEvent, SensorInstaladoEvent
│   ├── domain.repository         -- SateliteRepository
│   └── application               -- SateliteApplicationService
├── eventosclimaticos
│   ├── domain.model.evento       -- EventoClimatico (AR), TipoEvento, Intensidade, DataOcorrencia
│   ├── domain.model.observacao   -- Observacao (Entity), ValorColetado
│   ├── domain.model.alerta       -- Alerta (Entity), NivelRisco, DescricaoAlerta
│   ├── domain.service            -- ServicoDeteccaoEvento, PoliticaEmissaoAlerta (Domain Services)
│   ├── domain.event              -- EventoClimaticoDetectadoEvent, ObservacaoRegistradaEvent, AlertaEmitidoEvent
│   ├── domain.repository         -- EventoClimaticoRepository
│   └── application               -- MonitoramentoClimaticoApplicationService
├── infrastructure
│   ├── event                     -- InMemoryDomainEventPublisher
│   └── persistence.inmemory      -- InMemory{Satelite,Regiao,EventoClimatico}Repository (dados mockados)
└── Main                          -- demonstração end-to-end
```

---

## 5. Aggregates, invariantes e regras

### `Satelite` (Aggregate Root)
- Cria-se via factory `Satelite.lancar(...)` que dispara `SateliteLancadoEvent`.
- **Invariante:** não pode instalar dois sensores do mesmo `TipoSensor`.
- **Invariante:** máximo de 12 sensores.
- **Invariante:** satélite só pode ser `ativar()` se possui ao menos um sensor.
- **Invariante:** satélite `DESATIVADO` não volta a operar.
- `instalarSensor(...)` dispara `SensorInstaladoEvent`.

### `EventoClimatico` (Aggregate Root)
- Cria-se via `EventoClimatico.detectar(...)` que dispara `EventoClimaticoDetectadoEvent`.
- Contém `Observacao` e `Alerta` como entities internas — ciclo de vida atrelado ao evento.
- **Invariante:** se já existe alerta `CRITICO`, novos alertas devem ser de nível ≥ CRÍTICO.
- `adicionarObservacao(...)` dispara `ObservacaoRegistradaEvent`.
- `emitirAlerta(...)` dispara `AlertaEmitidoEvent`.

### `Regiao` (Aggregate Root)
- Encapsula `Coordenadas` com validação de latitude/longitude.
- Disponibiliza `contem(ponto, raioKm)` usando fórmula de Haversine.

---

## 6. Value Objects (imutáveis, autovalidados)

| VO                  | Bounded Context        | Regras                                            |
| ------------------- | ---------------------- | ------------------------------------------------- |
| `Coordenadas`       | Geografia              | latitude ∈ [-90, 90], longitude ∈ [-180, 180]     |
| `Pais`              | Geografia (kernel)     | não nulo, máx 50 caracteres                       |
| `NomeRegiao`        | Geografia              | não vazio, máx 100 caracteres                     |
| `NomeSatelite`      | Monitoramento Espacial | não vazio, máx 100 caracteres                     |
| `DataLancamento`    | Monitoramento Espacial | não pode ser futura                               |
| `Precisao`          | Monitoramento Espacial | percentual ∈ [0, 100]                             |
| `Intensidade`       | Eventos Climáticos     | ∈ [0, 100]; flags `ehCritica`, `ehAlta`, `ehModerada` |
| `DataOcorrencia`    | Eventos Climáticos     | não pode ser futura                               |
| `ValorColetado`     | Eventos Climáticos     | valor + unidade obrigatórios                      |
| `DescricaoAlerta`   | Eventos Climáticos     | não vazio, máx 300 caracteres                     |

Enums (também value objects): `StatusOperacional`, `TipoSensor`, `TipoEvento`, `NivelRisco`.

IDs como VOs: `SateliteId`, `SensorId`, `RegiaoId`, `EventoId`, `ObservacaoId`, `AlertaId` — UUIDs, imutáveis, com `equals/hashCode` por valor.

---

## 7. Domain Services

São operações de domínio que não pertencem naturalmente a um agregado:

### `ServicoDeteccaoEvento`
Recebe um conjunto de leituras de sensores de um satélite e produz um `EventoClimatico`:
- Verifica que o satélite está apto a coletar.
- Verifica que cada `SensorId` da leitura pertence ao satélite.
- Calcula a **intensidade agregada** com fator de ajuste por tipo (furacão e tornado pesam 1.20×; enchente e tempestade 1.10×; seca e onda de calor 1.05×; demais 1.00×).

### `PoliticaEmissaoAlerta`
Lê o evento e decide se um alerta deve ser emitido, com qual nível:
- Intensidade crítica (≥ 80) → `CRITICO`
- Intensidade alta (≥ 60) → `CRITICO` se furacão/tornado, senão `ALTO`
- Intensidade moderada (≥ 30) → `MODERADO`
- Senão → `BAIXO`

---

## 8. Domain Events

| Evento                          | Disparado em                                |
| ------------------------------- | ------------------------------------------- |
| `SateliteLancadoEvent`          | `Satelite.lancar(...)`                      |
| `SensorInstaladoEvent`          | `Satelite.instalarSensor(...)`              |
| `EventoClimaticoDetectadoEvent` | `EventoClimatico.detectar(...)`             |
| `ObservacaoRegistradaEvent`     | `EventoClimatico.adicionarObservacao(...)`  |
| `AlertaEmitidoEvent`            | `EventoClimatico.emitirAlerta(...)`         |

Eventos ficam *pendentes* no agregado (`AggregateRoot.consumirEventos()`) e são publicados pelo `Application Service` após salvar o agregado — padrão recomendado por Vaughn Vernon.

---

## 9. Repositórios

Definidos como **interfaces no domínio** (`SateliteRepository`, `RegiaoRepository`, `EventoClimaticoRepository`) e implementados na infraestrutura. As implementações atuais são in-memory (`InMemory*Repository`), com dados **mockados** via `HashMap` — não há banco real. Como o domínio depende apenas da interface, trocar a persistência no futuro não exige alterar nenhuma classe de domínio.

---

## 10. Aderência ao briefing do professor

| Critério                                                                                  | Onde está                                                                                                          |
| ----------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| Aderência ao briefing GS (10%) — domínio coerente com OrbitaSafe                          | Todo o código modela satélites, sensores, regiões, eventos, observações e alertas dentro do contexto OrbitaSafe    |
| Modelo DDD (30%) — Entities, VOs, Aggregates, Domain Services, Events, BCs                | Seções 3–8 deste README                                                                                            |
| Implementação Java (40%) — código limpo, boas práticas                                    | Factory methods, agregados com invariantes, VOs imutáveis com validação, separação `domain`/`application`/`infrastructure`, repositórios via interface |
| Integração com o projeto do grupo (20%) — DDD aplicado direto à solução em construção     | Mesmo domínio, mesmas regras de negócio e mesmas entidades que o grupo está desenvolvendo nas outras matérias      |
