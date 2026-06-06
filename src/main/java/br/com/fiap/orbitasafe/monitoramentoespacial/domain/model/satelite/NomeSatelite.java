package br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite;

import java.util.Objects;

public record NomeSatelite(String valor) {

    public NomeSatelite {
        Objects.requireNonNull(valor, "Nome do satelite nao pode ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("Nome do satelite nao pode ser vazio");
        }
        if (valor.length() > 100) {
            throw new IllegalArgumentException("Nome do satelite excede 100 caracteres");
        }
    }
}
