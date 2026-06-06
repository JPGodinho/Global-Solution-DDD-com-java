package br.com.fiap.orbitasafe.infrastructure.persistence.inmemory;

import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoClimatico;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.TipoEvento;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.repository.EventoClimaticoRepository;
import br.com.fiap.orbitasafe.geografia.domain.model.RegiaoId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryEventoClimaticoRepository implements EventoClimaticoRepository {

    private final Map<EventoId, EventoClimatico> dados = new HashMap<>();

    @Override
    public void salvar(EventoClimatico evento) {
        dados.put(evento.id(), evento);
    }

    @Override
    public Optional<EventoClimatico> buscarPorId(EventoId id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<EventoClimatico> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public void remover(EventoId id) {
        dados.remove(id);
    }

    @Override
    public List<EventoClimatico> buscarPorRegiao(RegiaoId regiaoId) {
        return dados.values().stream()
                .filter(e -> e.regiaoId().equals(regiaoId))
                .toList();
    }

    @Override
    public List<EventoClimatico> buscarPorTipo(TipoEvento tipo) {
        return dados.values().stream()
                .filter(e -> e.tipo() == tipo)
                .toList();
    }

    @Override
    public List<EventoClimatico> buscarCriticosSemAlerta() {
        return dados.values().stream()
                .filter(EventoClimatico::exigeAlertaImediato)
                .toList();
    }
}
