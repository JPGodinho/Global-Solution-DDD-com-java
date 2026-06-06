package br.com.fiap.orbitasafe.shared.domain;

public interface DomainEventPublisher {
    void publicar(DomainEvent evento);
}
