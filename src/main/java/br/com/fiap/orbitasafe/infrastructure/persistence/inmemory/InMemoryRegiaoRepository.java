package br.com.fiap.orbitasafe.infrastructure.persistence.inmemory;

import br.com.fiap.orbitasafe.geografia.domain.model.Regiao;
import br.com.fiap.orbitasafe.geografia.domain.model.RegiaoId;
import br.com.fiap.orbitasafe.geografia.domain.repository.RegiaoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryRegiaoRepository implements RegiaoRepository {

    private final Map<RegiaoId, Regiao> dados = new HashMap<>();

    @Override
    public void salvar(Regiao regiao) {
        dados.put(regiao.id(), regiao);
    }

    @Override
    public Optional<Regiao> buscarPorId(RegiaoId id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Regiao> listarTodos() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public void remover(RegiaoId id) {
        dados.remove(id);
    }

    @Override
    public List<Regiao> buscarPorPais(String pais) {
        return dados.values().stream()
                .filter(r -> r.pais().nome().equalsIgnoreCase(pais))
                .toList();
    }
}
