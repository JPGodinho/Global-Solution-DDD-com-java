package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Intensidade(BigDecimal valor) {

    public Intensidade {
        Objects.requireNonNull(valor, "Intensidade nao pode ser nula");
        if (valor.compareTo(BigDecimal.ZERO) < 0
                || valor.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Intensidade deve estar entre 0 e 100");
        }
    }

    public static Intensidade de(double valor) {
        return new Intensidade(BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP));
    }

    public boolean ehCritica() {
        return valor.compareTo(new BigDecimal("80")) >= 0;
    }

    public boolean ehAlta() {
        return valor.compareTo(new BigDecimal("60")) >= 0;
    }

    public boolean ehModerada() {
        return valor.compareTo(new BigDecimal("30")) >= 0;
    }
}
