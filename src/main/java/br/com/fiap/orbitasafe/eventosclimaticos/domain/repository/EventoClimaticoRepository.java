package br.com.fiap.orbitasafe.eventosclimaticos.domain.repository;

import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoClimatico;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoId;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.TipoEvento;
import br.com.fiap.orbitasafe.geografia.domain.model.RegiaoId;
import br.com.fiap.orbitasafe.shared.domain.Repository;

import java.util.List;

public interface EventoClimaticoRepository extends Repository<EventoClimatico, EventoId> {

    List<EventoClimatico> buscarPorRegiao(RegiaoId regiaoId);

    List<EventoClimatico> buscarPorTipo(TipoEvento tipo);

    List<EventoClimatico> buscarCriticosSemAlerta();
}
