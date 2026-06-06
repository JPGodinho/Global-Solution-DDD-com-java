package br.com.fiap.orbitasafe.infrastructure.persistence.inmemory;

import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.NomeSatelite;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.Satelite;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.SateliteId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.StatusOperacional;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.SensorId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.repository.SateliteRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemorySateliteRepository implements SateliteRepository {

    private final Map<SateliteId, Satelite> dados = new HashMap<>();

    @Override
    public void salvar(Satelite satelite) {
        dados.put(satelite.id(), satelite);
    }

    @Override
    public Optional<Satelite> buscarPorId(SateliteId id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Satelite> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public void remover(SateliteId id) {
        dados.remove(id);
    }

    @Override
    public Optional<Satelite> buscarPorNome(NomeSatelite nome) {
        return dados.values().stream()
                .filter(s -> s.nome().equals(nome))
                .findFirst();
    }

    @Override
    public Optional<Satelite> buscarPorSensor(SensorId sensorId) {
        return dados.values().stream()
                .filter(s -> s.possuiSensor(sensorId))
                .findFirst();
    }

    @Override
    public List<Satelite> listarAtivos() {
        return dados.values().stream()
                .filter(s -> s.status() == StatusOperacional.ATIVO)
                .toList();
    }
}
