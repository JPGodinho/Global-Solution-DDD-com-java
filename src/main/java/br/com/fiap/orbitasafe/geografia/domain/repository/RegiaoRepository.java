package br.com.fiap.orbitasafe.geografia.domain.repository;

import br.com.fiap.orbitasafe.geografia.domain.model.Regiao;
import br.com.fiap.orbitasafe.geografia.domain.model.RegiaoId;
import br.com.fiap.orbitasafe.shared.domain.Repository;

import java.util.List;

public interface RegiaoRepository extends Repository<Regiao, RegiaoId> {
    List<Regiao> buscarPorPais(String pais);
}
