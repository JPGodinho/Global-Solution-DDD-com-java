package br.com.fiap.orbitasafe.geografia.domain.model;

import java.util.Objects;

public record Pais(String nome) {

    public Pais {
        Objects.requireNonNull(nome, "Nome do pais nao pode ser nulo");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome do pais nao pode ser vazio");
        }
        if (nome.length() > 50) {
            throw new IllegalArgumentException("Nome do pais excede 50 caracteres");
        }
    }
}
