package br.com.fiap.orbitasafe.shared.domain;

public abstract class Entity<ID> {

    public abstract ID id();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity<?> other)) return false;
        return id() != null && id().equals(other.id());
    }

    @Override
    public int hashCode() {
        return id() == null ? 0 : id().hashCode();
    }
}
