package br.com.fiap.orbitasafe.monitoramentoespacial.domain.event;

import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.SateliteId;
import br.com.fiap.orbitasafe.shared.domain.DomainEvent;

import java.time.Instant;
import java.time.LocalDate;

public record SateliteLancadoEvent(
        SateliteId sateliteId,
        String nome,
        String paisOrigem,
        LocalDate dataLancamento,
        Instant ocorridoEm
) implements DomainEvent {

    public static SateliteLancadoEvent agora(SateliteId id, String nome, String pais, LocalDate dataLancamento) {
        return new SateliteLancadoEvent(id, nome, pais, dataLancamento, Instant.now());
    }
}
