package br.com.fiap.orbitasafe.geografia.application;

import br.com.fiap.orbitasafe.geografia.domain.model.Coordenadas;
import br.com.fiap.orbitasafe.geografia.domain.model.NomeRegiao;
import br.com.fiap.orbitasafe.geografia.domain.model.Pais;
import br.com.fiap.orbitasafe.geografia.domain.model.Regiao;
import br.com.fiap.orbitasafe.geografia.domain.model.RegiaoId;
import br.com.fiap.orbitasafe.geografia.domain.repository.RegiaoRepository;

import java.util.Objects;

public class RegiaoApplicationService {

    private final RegiaoRepository repository;

    public RegiaoApplicationService(RegiaoRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public RegiaoId cadastrarRegiao(String nome, String pais, double latitude, double longitude) {
        Regiao regiao = Regiao.cadastrar(
                new NomeRegiao(nome),
                new Pais(pais),
                new Coordenadas(latitude, longitude)
        );
        repository.salvar(regiao);
        return regiao.id();
    }
}
