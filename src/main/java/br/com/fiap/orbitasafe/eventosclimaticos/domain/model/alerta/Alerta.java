package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta;

import br.com.fiap.orbitasafe.shared.domain.Entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Alerta extends Entity<AlertaId> {

    private final AlertaId id;
    private final NivelRisco nivelRisco;
    private final LocalDateTime dataEmissao;
    private final DescricaoAlerta descricao;

    private Alerta(AlertaId id, NivelRisco nivelRisco, LocalDateTime dataEmissao, DescricaoAlerta descricao) {
        this.id = Objects.requireNonNull(id);
        this.nivelRisco = Objects.requireNonNull(nivelRisco);
        this.dataEmissao = Objects.requireNonNull(dataEmissao);
        this.descricao = Objects.requireNonNull(descricao);
    }

    public static Alerta emitir(NivelRisco nivel, DescricaoAlerta descricao) {
        return new Alerta(AlertaId.novo(), nivel, LocalDateTime.now(), descricao);
    }

    public static Alerta reconstituir(AlertaId id, NivelRisco nivel,
                                      LocalDateTime dataEmissao, DescricaoAlerta descricao) {
        return new Alerta(id, nivel, dataEmissao, descricao);
    }

    public boolean exigeNotificacaoImediata() {
        return nivelRisco.exigeNotificacaoImediata();
    }

    @Override
    public AlertaId id() {
        return id;
    }

    public NivelRisco nivelRisco() {
        return nivelRisco;
    }

    public LocalDateTime dataEmissao() {
        return dataEmissao;
    }

    public DescricaoAlerta descricao() {
        return descricao;
    }
}
