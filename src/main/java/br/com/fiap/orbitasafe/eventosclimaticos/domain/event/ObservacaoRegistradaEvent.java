package br.com.fiap.orbitasafe.eventosclimaticos.domain.event;

import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.observacao.ObservacaoId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.SensorId;
import br.com.fiap.orbitasafe.shared.domain.DomainEvent;

import java.time.Instant;

public record ObservacaoRegistradaEvent(
        EventoId eventoId,
        ObservacaoId observacaoId,
        SensorId sensorId,
        Instant ocorridoEm
) implements DomainEvent {

    public static ObservacaoRegistradaEvent agora(EventoId eventoId, ObservacaoId observacaoId, SensorId sensorId) {
        return new ObservacaoRegistradaEvent(eventoId, observacaoId, sensorId, Instant.now());
    }
}
