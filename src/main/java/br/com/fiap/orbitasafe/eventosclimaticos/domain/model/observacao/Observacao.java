package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.observacao;

import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.SensorId;
import br.com.fiap.orbitasafe.shared.domain.Entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Observacao extends Entity<ObservacaoId> {

    private final ObservacaoId id;
    private final SensorId sensorId;
    private final LocalDateTime dataColeta;
    private final ValorColetado valor;

    private Observacao(ObservacaoId id, SensorId sensorId, LocalDateTime dataColeta, ValorColetado valor) {
        this.id = Objects.requireNonNull(id);
        this.sensorId = Objects.requireNonNull(sensorId, "Observacao precisa referenciar um sensor");
        this.dataColeta = Objects.requireNonNull(dataColeta);
        this.valor = Objects.requireNonNull(valor);
        if (dataColeta.isAfter(LocalDateTime.now().plusMinutes(1))) {
            throw new IllegalArgumentException("Data de coleta nao pode ser futura");
        }
    }

    public static Observacao registrar(SensorId sensorId, LocalDateTime dataColeta, ValorColetado valor) {
        return new Observacao(ObservacaoId.novo(), sensorId, dataColeta, valor);
    }

    public static Observacao reconstituir(ObservacaoId id, SensorId sensorId,
                                          LocalDateTime dataColeta, ValorColetado valor) {
        return new Observacao(id, sensorId, dataColeta, valor);
    }

    @Override
    public ObservacaoId id() {
        return id;
    }

    public SensorId sensorId() {
        return sensorId;
    }

    public LocalDateTime dataColeta() {
        return dataColeta;
    }

    public ValorColetado valor() {
        return valor;
    }
}
