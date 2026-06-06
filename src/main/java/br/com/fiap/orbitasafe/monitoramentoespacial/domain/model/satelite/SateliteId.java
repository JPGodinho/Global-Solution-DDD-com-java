package br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite;

import java.util.Objects;
import java.util.UUID;

public final class SateliteId {

    private final String valor;

    private SateliteId(String valor) {
        this.valor = Objects.requireNonNull(valor, "SateliteId nao pode ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("SateliteId nao pode ser vazio");
        }
    }

    public static SateliteId novo() {
        return new SateliteId(UUID.randomUUID().toString());
    }

    public static SateliteId de(String valor) {
        return new SateliteId(valor);
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SateliteId other)) return false;
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
