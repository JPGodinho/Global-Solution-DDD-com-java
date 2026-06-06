package br.com.fiap.orbitasafe;

import br.com.fiap.orbitasafe.eventosclimaticos.application.MonitoramentoClimaticoApplicationService;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.event.AlertaEmitidoEvent;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.event.EventoClimaticoDetectadoEvent;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.Alerta;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoClimatico;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.TipoEvento;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.observacao.ValorColetado;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.repository.EventoClimaticoRepository;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.service.PoliticaEmissaoAlerta;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.service.ServicoDeteccaoEvento;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.service.ServicoDeteccaoEvento.LeituraSensor;
import br.com.fiap.orbitasafe.geografia.application.RegiaoApplicationService;
import br.com.fiap.orbitasafe.geografia.domain.model.RegiaoId;
import br.com.fiap.orbitasafe.geografia.domain.repository.RegiaoRepository;
import br.com.fiap.orbitasafe.infrastructure.event.InMemoryDomainEventPublisher;
import br.com.fiap.orbitasafe.infrastructure.persistence.inmemory.InMemoryEventoClimaticoRepository;
import br.com.fiap.orbitasafe.infrastructure.persistence.inmemory.InMemoryRegiaoRepository;
import br.com.fiap.orbitasafe.infrastructure.persistence.inmemory.InMemorySateliteRepository;
import br.com.fiap.orbitasafe.monitoramentoespacial.application.SateliteApplicationService;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.event.SateliteLancadoEvent;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.Satelite;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.SateliteId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.SensorId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.TipoSensor;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.repository.SateliteRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== OrbitaSafe :: Demonstracao DDD ===\n");

        InMemoryDomainEventPublisher publisher = new InMemoryDomainEventPublisher();
        registrarHandlers(publisher);

        SateliteRepository sateliteRepo = new InMemorySateliteRepository();
        RegiaoRepository regiaoRepo = new InMemoryRegiaoRepository();
        EventoClimaticoRepository eventoRepo = new InMemoryEventoClimaticoRepository();

        RegiaoApplicationService regiaoService = new RegiaoApplicationService(regiaoRepo);
        SateliteApplicationService sateliteService = new SateliteApplicationService(sateliteRepo, publisher);
        MonitoramentoClimaticoApplicationService monitoramento =
                new MonitoramentoClimaticoApplicationService(
                        eventoRepo, sateliteRepo, regiaoRepo,
                        new ServicoDeteccaoEvento(),
                        new PoliticaEmissaoAlerta(),
                        publisher
                );

        System.out.println("[1] Cadastrando regioes monitoradas");
        RegiaoId litoralSP = regiaoService.cadastrarRegiao("Litoral Norte SP", "Brasil", -23.6986, -45.4072);
        RegiaoId amazonasAM = regiaoService.cadastrarRegiao("Amazonas Central", "Brasil", -3.4653, -62.2159);
        System.out.println("   - Litoral Norte SP: " + litoralSP);
        System.out.println("   - Amazonas Central: " + amazonasAM + "\n");

        System.out.println("[2] Lancando satelite e instalando sensores");
        SateliteId orbiSat1 = sateliteService.lancarSatelite("OrbiSat-1", "Brasil", LocalDate.of(2025, 8, 12));
        SensorId sensorRadar = sateliteService.instalarSensor(orbiSat1, "Radar Banda-X", TipoSensor.RADAR_PRECIPITACAO, 92.5);
        SensorId sensorInfra = sateliteService.instalarSensor(orbiSat1, "Imageador IR", TipoSensor.INFRAVERMELHO, 88.0);
        SensorId sensorVento = sateliteService.instalarSensor(orbiSat1, "Anemometro Doppler", TipoSensor.VENTO, 90.0);
        sateliteService.ativarSatelite(orbiSat1);
        System.out.println("   - Satelite OrbiSat-1 ATIVO com 3 sensores\n");

        System.out.println("[3] Tentando reinstalar sensor RADAR (deve falhar pela invariante)");
        try {
            sateliteService.instalarSensor(orbiSat1, "Radar Duplicado", TipoSensor.RADAR_PRECIPITACAO, 80.0);
        } catch (IllegalStateException ex) {
            System.out.println("   - Invariante respeitada: " + ex.getMessage() + "\n");
        }

        System.out.println("[4] Detectando tempestade severa no Litoral Norte SP");
        EventoId tempestade = monitoramento.detectarEvento(
                TipoEvento.TEMPESTADE,
                litoralSP,
                orbiSat1,
                List.of(
                        new LeituraSensor(sensorRadar, TipoSensor.RADAR_PRECIPITACAO,
                                LocalDateTime.now().minusMinutes(15), ValorColetado.de(78.5, "mm/h")),
                        new LeituraSensor(sensorVento, TipoSensor.VENTO,
                                LocalDateTime.now().minusMinutes(10), ValorColetado.de(82.0, "km/h")),
                        new LeituraSensor(sensorInfra, TipoSensor.INFRAVERMELHO,
                                LocalDateTime.now().minusMinutes(5), ValorColetado.de(70.0, "indice"))
                )
        );
        imprimirEvento(eventoRepo, tempestade);

        System.out.println("\n[5] Detectando seca moderada no Amazonas Central");
        EventoId seca = monitoramento.detectarEvento(
                TipoEvento.SECA,
                amazonasAM,
                orbiSat1,
                List.of(
                        new LeituraSensor(sensorInfra, TipoSensor.INFRAVERMELHO,
                                LocalDateTime.now().minusHours(2), ValorColetado.de(45.0, "indice"))
                )
        );
        imprimirEvento(eventoRepo, seca);

        System.out.println("\n[6] Verificando estado do satelite apos uso");
        Satelite satelite = sateliteRepo.buscarPorId(orbiSat1).orElseThrow();
        System.out.println("   - Status: " + satelite.status());
        System.out.println("   - Sensores: " + satelite.sensores().size());
        System.out.println("   - Pode coletar: " + satelite.podeColetar());

        System.out.println("\n[7] Historico de Domain Events publicados: " + publisher.historico().size());
        publisher.historico().forEach(e -> System.out.println("   * " + e.getClass().getSimpleName()));

        System.out.println("\n=== Fim da demonstracao ===");
    }

    private static void registrarHandlers(InMemoryDomainEventPublisher publisher) {
        publisher.assinar(SateliteLancadoEvent.class, e ->
                System.out.println("   [evt] Satelite lancado: " + e.nome() + " (" + e.paisOrigem() + ")"));
        publisher.assinar(EventoClimaticoDetectadoEvent.class, e ->
                System.out.println("   [evt] Evento detectado: " + e.tipo()
                        + " | intensidade=" + e.intensidade().valor()));
        publisher.assinar(AlertaEmitidoEvent.class, e ->
                System.out.println("   [evt] Alerta emitido: nivel=" + e.nivelRisco()));
    }

    private static void imprimirEvento(EventoClimaticoRepository repo, EventoId id) {
        EventoClimatico evento = repo.buscarPorId(id).orElseThrow();
        System.out.println("   - Evento " + evento.tipo()
                + " | intensidade=" + evento.intensidade().valor()
                + " | observacoes=" + evento.totalObservacoes()
                + " | alertas=" + evento.alertas().size());
        for (Alerta a : evento.alertas()) {
            System.out.println("     -> " + a.nivelRisco() + ": " + a.descricao().texto());
        }
    }
}
