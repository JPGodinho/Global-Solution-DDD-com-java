package br.com.fiap.orbitasafe.shared.domain;

import java.util.List;
import java.util.Optional;

public interface Repository<T extends AggregateRoot<ID>, ID> {
    void salvar(T aggregate);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
    void remover(ID id);
}
