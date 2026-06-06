package br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite;

public enum StatusOperacional {
    EM_PREPARACAO,
    ATIVO,
    EM_MANUTENCAO,
    DESATIVADO;

    public boolean podeColetar() {
        return this == ATIVO;
    }
}
