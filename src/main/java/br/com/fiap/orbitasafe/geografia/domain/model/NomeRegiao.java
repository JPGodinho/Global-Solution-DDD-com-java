package br.com.fiap.orbitasafe.geografia.domain.model;

import java.util.Objects;

public record NomeRegiao(String valor) {

    public NomeRegiao {
        Objects.requireNonNull(valor, "Nome da regiao nao pode ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException("Nome da regiao nao pode ser vazio");
        }
        if (valor.length() > 100) {
            throw new IllegalArgumentException("Nome da regiao excede 100 caracteres");
        }
    }
}
