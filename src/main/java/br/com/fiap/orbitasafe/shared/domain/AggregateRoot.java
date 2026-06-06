package br.com.fiap.orbitasafe.shared.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<ID> {

    private final List<DomainEvent> eventosPendentes = new ArrayList<>();

    public abstract ID id();

    protected void registrarEvento(DomainEvent evento) {
        eventosPendentes.add(evento);
    }

    public List<DomainEvent> consumirEventos() {
        List<DomainEvent> copia = new ArrayList<>(eventosPendentes);
        eventosPendentes.clear();
        return Collections.unmodifiableList(copia);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregateRoot<?> other)) return false;
        return id() != null && id().equals(other.id());
    }

    @Override
    public int hashCode() {
        return id() == null ? 0 : id().hashCode();
    }
}
