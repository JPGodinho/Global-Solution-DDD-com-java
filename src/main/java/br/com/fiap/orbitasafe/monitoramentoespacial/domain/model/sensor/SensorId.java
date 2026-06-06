package br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor;

import java.util.Objects;
import java.util.UUID;

public final class SensorId {

    private final String valor;

    private SensorId(String valor) {
        this.valor = Objects.requireNonNull(valor, "SensorId nao pode ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("SensorId nao pode ser vazio");
        }
    }

    public static SensorId novo() {
        return new SensorId(UUID.randomUUID().toString());
    }

    public static SensorId de(String valor) {
        return new SensorId(valor);
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorId other)) return false;
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
