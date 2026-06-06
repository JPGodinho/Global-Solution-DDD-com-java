package br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Precisao(BigDecimal percentual) {

    public Precisao {
        Objects.requireNonNull(percentual, "Precisao nao pode ser nula");
        if (percentual.compareTo(BigDecimal.ZERO) < 0
                || percentual.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Precisao deve estar entre 0 e 100");
        }
    }

    public static Precisao de(double valor) {
        return new Precisao(BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP));
    }

    public boolean isAlta() {
        return percentual.compareTo(new BigDecimal("90")) >= 0;
    }
}
