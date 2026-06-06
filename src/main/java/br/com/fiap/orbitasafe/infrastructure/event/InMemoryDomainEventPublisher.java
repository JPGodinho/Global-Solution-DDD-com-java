package br.com.fiap.orbitasafe.infrastructure.event;

import br.com.fiap.orbitasafe.shared.domain.AggregateRoot;
import br.com.fiap.orbitasafe.shared.domain.DomainEvent;
import br.com.fiap.orbitasafe.shared.domain.DomainEventPublisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InMemoryDomainEventPublisher implements DomainEventPublisher {

    private final Map<Class<? extends DomainEvent>, List<Consumer<DomainEvent>>> assinantes = new HashMap<>();
    private final List<DomainEvent> historico = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public <E extends DomainEvent> void assinar(Class<E> tipo, Consumer<E> handler) {
        assinantes.computeIfAbsent(tipo, t -> new ArrayList<>())
                  .add((Consumer<DomainEvent>) handler);
    }

    @Override
    public void publicar(DomainEvent evento) {
        historico.add(evento);
        List<Consumer<DomainEvent>> handlers = assinantes.get(evento.getClass());
        if (handlers != null) {
            handlers.forEach(h -> h.accept(evento));
        }
    }

    public void publicarPendentes(AggregateRoot<?> aggregate) {
        aggregate.consumirEventos().forEach(this::publicar);
    }

    public List<DomainEvent> historico() {
        return List.copyOf(historico);
    }
}
