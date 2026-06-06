package br.com.fiap.orbitasafe.monitoramentoespacial.domain.event;

import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.SateliteId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.SensorId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.TipoSensor;
import br.com.fiap.orbitasafe.shared.domain.DomainEvent;

import java.time.Instant;

public record SensorInstaladoEvent(
        SateliteId sateliteId,
        SensorId sensorId,
        TipoSensor tipo,
        Instant ocorridoEm
) implements DomainEvent {

    public static SensorInstaladoEvent agora(SateliteId sateliteId, SensorId sensorId, TipoSensor tipo) {
        return new SensorInstaladoEvent(sateliteId, sensorId, tipo, Instant.now());
    }
}
