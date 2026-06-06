package br.com.fiap.orbitasafe.eventosclimaticos.domain.event;

import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.AlertaId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.NivelRisco;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoId;
import br.com.fiap.orbitasafe.shared.domain.DomainEvent;

import java.time.Instant;

public record AlertaEmitidoEvent(
        EventoId eventoId,
        AlertaId alertaId,
        NivelRisco nivelRisco,
        Instant ocorridoEm
) implements DomainEvent {

    public static AlertaEmitidoEvent agora(EventoId eventoId, AlertaId alertaId, NivelRisco nivel) {
        return new AlertaEmitidoEvent(eventoId, alertaId, nivel, Instant.now());
    }
}
