package br.com.fiap.orbitasafe.geografia.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class RegiaoId {

    private final String valor;

    private RegiaoId(String valor) {
        this.valor = Objects.requireNonNull(valor, "RegiaoId nao pode ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("RegiaoId nao pode ser vazio");
        }
    }

    public static RegiaoId novo() {
        return new RegiaoId(UUID.randomUUID().toString());
    }

    public static RegiaoId de(String valor) {
        return new RegiaoId(valor);
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegiaoId other)) return false;
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
