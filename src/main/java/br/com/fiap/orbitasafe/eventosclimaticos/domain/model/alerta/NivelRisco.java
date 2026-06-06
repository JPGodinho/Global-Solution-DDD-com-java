package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta;

public enum NivelRisco {
    BAIXO(1),
    MODERADO(2),
    ALTO(3),
    CRITICO(4);

    private final int prioridade;

    NivelRisco(int prioridade) {
        this.prioridade = prioridade;
    }

    public int prioridade() {
        return prioridade;
    }

    public boolean exigeNotificacaoImediata() {
        return this == ALTO || this == CRITICO;
    }
}
