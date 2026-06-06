package br.com.fiap.orbitasafe.eventosclimaticos.domain.service;

import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.AlertaId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.DescricaoAlerta;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.NivelRisco;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoClimatico;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.Intensidade;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.TipoEvento;

import java.util.Optional;

public class PoliticaEmissaoAlerta {

    public Optional<AlertaId> avaliarEEmitir(EventoClimatico evento) {
        NivelRisco nivel = determinarNivel(evento.tipo(), evento.intensidade());
        if (nivel == NivelRisco.BAIXO && evento.possuiAlerta()) {
            return Optional.empty();
        }
        DescricaoAlerta descricao = construirDescricao(evento, nivel);
        AlertaId alertaId = evento.emitirAlerta(nivel, descricao);
        return Optional.of(alertaId);
    }

    private NivelRisco determinarNivel(TipoEvento tipo, Intensidade intensidade) {
        if (intensidade.ehCritica()) {
            return NivelRisco.CRITICO;
        }
        if (intensidade.ehAlta()) {
            return (tipo == TipoEvento.FURACAO || tipo == TipoEvento.TORNADO)
                    ? NivelRisco.CRITICO
                    : NivelRisco.ALTO;
        }
        if (intensidade.ehModerada()) {
            return NivelRisco.MODERADO;
        }
        return NivelRisco.BAIXO;
    }

    private DescricaoAlerta construirDescricao(EventoClimatico evento, NivelRisco nivel) {
        String texto = String.format(
                "Alerta %s: evento %s com intensidade %s detectado em %s.",
                nivel.name(),
                evento.tipo().name(),
                evento.intensidade().valor().toPlainString(),
                evento.dataOcorrencia().dataHora()
        );
        return new DescricaoAlerta(texto);
    }
}
