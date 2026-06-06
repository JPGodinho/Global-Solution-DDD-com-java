package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta;

import java.util.Objects;
import java.util.UUID;

public final class AlertaId {

    private final String valor;

    private AlertaId(String valor) {
        this.valor = Objects.requireNonNull(valor, "AlertaId nao pode ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("AlertaId nao pode ser vazio");
        }
    }

    public static AlertaId novo() {
        return new AlertaId(UUID.randomUUID().toString());
    }

    public static AlertaId de(String valor) {
        return new AlertaId(valor);
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlertaId other)) return false;
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
