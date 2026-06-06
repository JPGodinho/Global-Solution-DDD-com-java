package br.com.fiap.orbitasafe.eventosclimaticos.application;

import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.alerta.AlertaId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoClimatico;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.TipoEvento;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.repository.EventoClimaticoRepository;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.service.PoliticaEmissaoAlerta;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.service.ServicoDeteccaoEvento;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.service.ServicoDeteccaoEvento.LeituraSensor;
import br.com.fiap.orbitasafe.geografia.domain.model.RegiaoId;
import br.com.fiap.orbitasafe.geografia.domain.repository.RegiaoRepository;
import br.com.fiap.orbitasafe.infrastructure.event.InMemoryDomainEventPublisher;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.Satelite;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.SateliteId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.repository.SateliteRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MonitoramentoClimaticoApplicationService {

    private final EventoClimaticoRepository eventoRepository;
    private final SateliteRepository sateliteRepository;
    private final RegiaoRepository regiaoRepository;
    private final ServicoDeteccaoEvento servicoDeteccao;
    private final PoliticaEmissaoAlerta politicaAlerta;
    private final InMemoryDomainEventPublisher publisher;

    public MonitoramentoClimaticoApplicationService(
            EventoClimaticoRepository eventoRepository,
            SateliteRepository sateliteRepository,
            RegiaoRepository regiaoRepository,
            ServicoDeteccaoEvento servicoDeteccao,
            PoliticaEmissaoAlerta politicaAlerta,
            InMemoryDomainEventPublisher publisher) {
        this.eventoRepository = Objects.requireNonNull(eventoRepository);
        this.sateliteRepository = Objects.requireNonNull(sateliteRepository);
        this.regiaoRepository = Objects.requireNonNull(regiaoRepository);
        this.servicoDeteccao = Objects.requireNonNull(servicoDeteccao);
        this.politicaAlerta = Objects.requireNonNull(politicaAlerta);
        this.publisher = Objects.requireNonNull(publisher);
    }

    public EventoId detectarEvento(
            TipoEvento tipo,
            RegiaoId regiaoId,
            SateliteId sateliteId,
            List<LeituraSensor> leituras) {

        regiaoRepository.buscarPorId(regiaoId)
                .orElseThrow(() -> new IllegalArgumentException("Regiao nao encontrada: " + regiaoId));
        Satelite satelite = sateliteRepository.buscarPorId(sateliteId)
                .orElseThrow(() -> new IllegalArgumentException("Satelite nao encontrado: " + sateliteId));

        EventoClimatico evento = servicoDeteccao.detectarApartirDeObservacoes(tipo, regiaoId, satelite, leituras);
        politicaAlerta.avaliarEEmitir(evento);

        eventoRepository.salvar(evento);
        publisher.publicarPendentes(evento);
        return evento.id();
    }

    public Optional<AlertaId> reavaliarAlerta(EventoId eventoId) {
        EventoClimatico evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado: " + eventoId));
        Optional<AlertaId> alertaId = politicaAlerta.avaliarEEmitir(evento);
        eventoRepository.salvar(evento);
        publisher.publicarPendentes(evento);
        return alertaId;
    }

    public List<EventoClimatico> eventosDaRegiao(RegiaoId regiaoId) {
        return eventoRepository.buscarPorRegiao(regiaoId);
    }
}
