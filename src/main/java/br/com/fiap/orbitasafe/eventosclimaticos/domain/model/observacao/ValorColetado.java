package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.observacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record ValorColetado(BigDecimal valor, String unidade) {

    public ValorColetado {
        Objects.requireNonNull(valor, "Valor coletado nao pode ser nulo");
        Objects.requireNonNull(unidade, "Unidade nao pode ser nula");
        if (unidade.isBlank()) {
            throw new IllegalArgumentException("Unidade nao pode ser vazia");
        }
    }

    public static ValorColetado de(double valor, String unidade) {
        return new ValorColetado(BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP), unidade);
    }
}
