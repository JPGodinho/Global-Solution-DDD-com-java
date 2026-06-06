package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento;

import java.time.LocalDateTime;
import java.util.Objects;

public record DataOcorrencia(LocalDateTime dataHora) {

    public DataOcorrencia {
        Objects.requireNonNull(dataHora, "Data de ocorrencia nao pode ser nula");
        if (dataHora.isAfter(LocalDateTime.now().plusMinutes(1))) {
            throw new IllegalArgumentException("Data de ocorrencia nao pode ser futura");
        }
    }

    public static DataOcorrencia agora() {
        return new DataOcorrencia(LocalDateTime.now());
    }
}
