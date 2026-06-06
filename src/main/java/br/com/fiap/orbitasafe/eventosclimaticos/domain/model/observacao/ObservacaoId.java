package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.observacao;

import java.util.Objects;
import java.util.UUID;

public final class ObservacaoId {

    private final String valor;

    private ObservacaoId(String valor) {
        this.valor = Objects.requireNonNull(valor, "ObservacaoId nao pode ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("ObservacaoId nao pode ser vazio");
        }
    }

    public static ObservacaoId novo() {
        return new ObservacaoId(UUID.randomUUID().toString());
    }

    public static ObservacaoId de(String valor) {
        return new ObservacaoId(valor);
    }

    public String valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObservacaoId other)) return false;
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
