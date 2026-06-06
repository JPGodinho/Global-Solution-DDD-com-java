package br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite;

import java.time.LocalDate;
import java.util.Objects;

public record DataLancamento(LocalDate data) {

    public DataLancamento {
        Objects.requireNonNull(data, "Data de lancamento nao pode ser nula");
        if (data.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de lancamento nao pode ser futura");
        }
    }
}
