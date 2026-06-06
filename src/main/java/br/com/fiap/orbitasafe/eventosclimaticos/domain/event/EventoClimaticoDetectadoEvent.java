package br.com.fiap.orbitasafe.eventosclimaticos.domain.event;

import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.Intensidade;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.TipoEvento;
import br.com.fiap.orbitasafe.geografia.domain.model.RegiaoId;
import br.com.fiap.orbitasafe.shared.domain.DomainEvent;

import java.time.Instant;
import java.time.LocalDateTime;

public record EventoClimaticoDetectadoEvent(
        EventoId eventoId,
        RegiaoId regiaoId,
        TipoEvento tipo,
        Intensidade intensidade,
        LocalDateTime dataOcorrencia,
        Instant ocorridoEm
) implements DomainEvent {

    public static EventoClimaticoDetectadoEvent agora(EventoId id, RegiaoId regiaoId,
                                                      TipoEvento tipo, Intensidade intensidade,
                                                      LocalDateTime dataOcorrencia) {
        return new EventoClimaticoDetectadoEvent(id, regiaoId, tipo, intensidade, dataOcorrencia, Instant.now());
    }
}
