package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta;

import java.util.Objects;

public record DescricaoAlerta(String texto) {

    public DescricaoAlerta {
        Objects.requireNonNull(texto, "Descricao nao pode ser nula");
        if (texto.isBlank()) {
            throw new IllegalArgumentException("Descricao nao pode ser vazia");
        }
        if (texto.length() > 300) {
            throw new IllegalArgumentException("Descricao excede 300 caracteres");
        }
    }
}
