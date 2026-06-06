package br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite;

import br.com.fiap.orbitasafe.geografia.domain.model.Pais;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.event.SateliteLancadoEvent;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.event.SensorInstaladoEvent;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.Precisao;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.Sensor;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.SensorId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.TipoSensor;
import br.com.fiap.orbitasafe.shared.domain.AggregateRoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Satelite extends AggregateRoot<SateliteId> {

    private static final int LIMITE_SENSORES = 12;

    private final SateliteId id;
    private final NomeSatelite nome;
    private final Pais paisOrigem;
    private final DataLancamento dataLancamento;
    private StatusOperacional status;
    private final List<Sensor> sensores;

    private Satelite(SateliteId id, NomeSatelite nome, Pais paisOrigem,
                     DataLancamento dataLancamento, StatusOperacional status,
                     List<Sensor> sensores) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        this.paisOrigem = Objects.requireNonNull(paisOrigem);
        this.dataLancamento = Objects.requireNonNull(dataLancamento);
        this.status = Objects.requireNonNull(status);
        this.sensores = new ArrayList<>(Objects.requireNonNull(sensores));
    }

    public static Satelite lancar(NomeSatelite nome, Pais paisOrigem, DataLancamento dataLancamento) {
        Satelite satelite = new Satelite(
                SateliteId.novo(),
                nome,
                paisOrigem,
                dataLancamento,
                StatusOperacional.EM_PREPARACAO,
                new ArrayList<>()
        );
        satelite.registrarEvento(SateliteLancadoEvent.agora(
                satelite.id, nome.valor(), paisOrigem.nome(), dataLancamento.data()
        ));
        return satelite;
    }

    public static Satelite reconstituir(SateliteId id, NomeSatelite nome, Pais paisOrigem,
                                        DataLancamento dataLancamento, StatusOperacional status,
                                        List<Sensor> sensores) {
        return new Satelite(id, nome, paisOrigem, dataLancamento, status, sensores);
    }

    public SensorId instalarSensor(String nomeSensor, TipoSensor tipo, Precisao precisao) {
        if (status == StatusOperacional.DESATIVADO) {
            throw new IllegalStateException("Nao e possivel instalar sensor em satelite desativado");
        }
        if (sensores.size() >= LIMITE_SENSORES) {
            throw new IllegalStateException("Limite de " + LIMITE_SENSORES + " sensores atingido");
        }
        boolean tipoJaPresente = sensores.stream().anyMatch(s -> s.tipo() == tipo);
        if (tipoJaPresente) {
            throw new IllegalStateException("Sensor do tipo " + tipo + " ja instalado neste satelite");
        }
        Sensor sensor = Sensor.instalar(nomeSensor, tipo, precisao);
        sensores.add(sensor);
        registrarEvento(SensorInstaladoEvent.agora(id, sensor.id(), tipo));
        return sensor.id();
    }

    public void ativar() {
        if (sensores.isEmpty()) {
            throw new IllegalStateException("Satelite precisa de ao menos um sensor para ser ativado");
        }
        if (status == StatusOperacional.DESATIVADO) {
            throw new IllegalStateException("Satelite desativado nao pode voltar a operar");
        }
        this.status = StatusOperacional.ATIVO;
    }

    public void colocarEmManutencao() {
        if (status != StatusOperacional.ATIVO) {
            throw new IllegalStateException("So satelites ativos podem entrar em manutencao");
        }
        this.status = StatusOperacional.EM_MANUTENCAO;
    }

    public void desativar() {
        this.status = StatusOperacional.DESATIVADO;
    }

    public Optional<Sensor> sensor(SensorId sensorId) {
        return sensores.stream().filter(s -> s.id().equals(sensorId)).findFirst();
    }

    public boolean possuiSensor(SensorId sensorId) {
        return sensor(sensorId).isPresent();
    }

    public boolean podeColetar() {
        return status.podeColetar() && !sensores.isEmpty();
    }

    @Override
    public SateliteId id() {
        return id;
    }

    public NomeSatelite nome() {
        return nome;
    }

    public Pais paisOrigem() {
        return paisOrigem;
    }

    public DataLancamento dataLancamento() {
        return dataLancamento;
    }

    public StatusOperacional status() {
        return status;
    }

    public List<Sensor> sensores() {
        return Collections.unmodifiableList(sensores);
    }
}
