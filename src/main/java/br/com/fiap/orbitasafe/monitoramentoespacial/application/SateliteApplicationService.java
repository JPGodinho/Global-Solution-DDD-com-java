package br.com.fiap.orbitasafe.monitoramentoespacial.application;

import br.com.fiap.orbitasafe.geografia.domain.model.Pais;
import br.com.fiap.orbitasafe.infrastructure.event.InMemoryDomainEventPublisher;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.DataLancamento;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.NomeSatelite;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.Satelite;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.SateliteId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.Precisao;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.SensorId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.TipoSensor;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.repository.SateliteRepository;

import java.time.LocalDate;
import java.util.Objects;

public class SateliteApplicationService {

    private final SateliteRepository repository;
    private final InMemoryDomainEventPublisher publisher;

    public SateliteApplicationService(SateliteRepository repository, InMemoryDomainEventPublisher publisher) {
        this.repository = Objects.requireNonNull(repository);
        this.publisher = Objects.requireNonNull(publisher);
    }

    public SateliteId lancarSatelite(String nome, String paisOrigem, LocalDate dataLancamento) {
        repository.buscarPorNome(new NomeSatelite(nome)).ifPresent(s -> {
            throw new IllegalStateException("Ja existe satelite com o nome: " + nome);
        });
        Satelite satelite = Satelite.lancar(
                new NomeSatelite(nome),
                new Pais(paisOrigem),
                new DataLancamento(dataLancamento)
        );
        repository.salvar(satelite);
        publisher.publicarPendentes(satelite);
        return satelite.id();
    }

    public SensorId instalarSensor(SateliteId sateliteId, String nomeSensor, TipoSensor tipo, double precisaoPercentual) {
        Satelite satelite = repository.buscarPorId(sateliteId)
                .orElseThrow(() -> new IllegalArgumentException("Satelite nao encontrado: " + sateliteId));
        SensorId sensorId = satelite.instalarSensor(nomeSensor, tipo, Precisao.de(precisaoPercentual));
        repository.salvar(satelite);
        publisher.publicarPendentes(satelite);
        return sensorId;
    }

    public void ativarSatelite(SateliteId sateliteId) {
        Satelite satelite = repository.buscarPorId(sateliteId)
                .orElseThrow(() -> new IllegalArgumentException("Satelite nao encontrado: " + sateliteId));
        satelite.ativar();
        repository.salvar(satelite);
        publisher.publicarPendentes(satelite);
    }
}
