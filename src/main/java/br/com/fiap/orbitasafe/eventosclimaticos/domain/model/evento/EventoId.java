package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento;

import java.util.Objects;
import java.util.UUID;

public final class EventoId {

    private final String valor;

    private EventoId(String valor) {
        this.valor = Objects.requireNonNull(valor, "EventoId nao pode ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("EventoId nao pode ser vazio");
        }
    }

    public static EventoId novo() {
        return new EventoId(UUID.randomUUID().toString());
    }

    public static EventoId de(String valor) {
        return new EventoId(valor);
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventoId other)) return false;
        return valor.equals(other.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor;
    }
}
