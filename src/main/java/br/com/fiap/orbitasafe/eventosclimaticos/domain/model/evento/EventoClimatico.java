package br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento;

import br.com.fiap.orbitasafe.eventosclimaticos.domain.event.AlertaEmitidoEvent;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.event.EventoClimaticoDetectadoEvent;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.event.ObservacaoRegistradaEvent;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.Alerta;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.AlertaId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.DescricaoAlerta;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.NivelRisco;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.observacao.Observacao;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.observacao.ObservacaoId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.observacao.ValorColetado;
import br.com.fiap.orbitasafe.geografia.domain.model.RegiaoId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.SensorId;
import br.com.fiap.orbitasafe.shared.domain.AggregateRoot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EventoClimatico extends AggregateRoot<EventoId> {

    private final EventoId id;
    private final TipoEvento tipo;
    private Intensidade intensidade;
    private final DataOcorrencia dataOcorrencia;
    private final RegiaoId regiaoId;
    private final List<Observacao> observacoes;
    private final List<Alerta> alertas;

    private EventoClimatico(EventoId id, TipoEvento tipo, Intensidade intensidade,
                            DataOcorrencia dataOcorrencia, RegiaoId regiaoId,
                            List<Observacao> observacoes, List<Alerta> alertas) {
        this.id = Objects.requireNonNull(id);
        this.tipo = Objects.requireNonNull(tipo);
        this.intensidade = Objects.requireNonNull(intensidade);
        this.dataOcorrencia = Objects.requireNonNull(dataOcorrencia);
        this.regiaoId = Objects.requireNonNull(regiaoId, "Evento precisa estar associado a uma regiao");
        this.observacoes = new ArrayList<>(Objects.requireNonNull(observacoes));
        this.alertas = new ArrayList<>(Objects.requireNonNull(alertas));
    }

    public static EventoClimatico detectar(TipoEvento tipo, Intensidade intensidade,
                                           DataOcorrencia dataOcorrencia, RegiaoId regiaoId) {
        EventoClimatico evento = new EventoClimatico(
                EventoId.novo(), tipo, intensidade, dataOcorrencia, regiaoId,
                new ArrayList<>(), new ArrayList<>()
        );
        evento.registrarEvento(EventoClimaticoDetectadoEvent.agora(
                evento.id, regiaoId, tipo, intensidade, dataOcorrencia.dataHora()
        ));
        return evento;
    }

    public static EventoClimatico reconstituir(EventoId id, TipoEvento tipo, Intensidade intensidade,
                                               DataOcorrencia dataOcorrencia, RegiaoId regiaoId,
                                               List<Observacao> observacoes, List<Alerta> alertas) {
        return new EventoClimatico(id, tipo, intensidade, dataOcorrencia, regiaoId, observacoes, alertas);
    }

    public ObservacaoId adicionarObservacao(SensorId sensorId, LocalDateTime dataColeta, ValorColetado valor) {
        Observacao observacao = Observacao.registrar(sensorId, dataColeta, valor);
        observacoes.add(observacao);
        registrarEvento(ObservacaoRegistradaEvent.agora(id, observacao.id(), sensorId));
        return observacao.id();
    }

    public AlertaId emitirAlerta(NivelRisco nivel, DescricaoAlerta descricao) {
        boolean jaTemCritico = alertas.stream().anyMatch(a -> a.nivelRisco() == NivelRisco.CRITICO);
        if (jaTemCritico && nivel != NivelRisco.CRITICO) {
            throw new IllegalStateException(
                    "Evento ja possui alerta CRITICO; novos alertas devem ser CRITICO ou superiores"
            );
        }
        Alerta alerta = Alerta.emitir(nivel, descricao);
        alertas.add(alerta);
        registrarEvento(AlertaEmitidoEvent.agora(id, alerta.id(), nivel));
        return alerta.id();
    }

    public void atualizarIntensidade(Intensidade nova) {
        this.intensidade = Objects.requireNonNull(nova);
    }

    public boolean exigeAlertaImediato() {
        return intensidade.ehCritica() && alertas.isEmpty();
    }

    public boolean possuiAlerta() {
        return !alertas.isEmpty();
    }

    public int totalObservacoes() {
        return observacoes.size();
    }

    @Override
    public EventoId id() {
        return id;
    }

    public TipoEvento tipo() {
        return tipo;
    }

    public Intensidade intensidade() {
        return intensidade;
    }

    public DataOcorrencia dataOcorrencia() {
        return dataOcorrencia;
    }

    public RegiaoId regiaoId() {
        return regiaoId;
    }

    public List<Observacao> observacoes() {
        return Collections.unmodifiableList(observacoes);
    }

    public List<Alerta> alertas() {
        return Collections.unmodifiableList(alertas);
    }
}
