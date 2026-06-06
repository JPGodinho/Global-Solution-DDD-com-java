package br.com.fiap.orbitasafe.geografia.domain.model;

import br.com.fiap.orbitasafe.shared.domain.AggregateRoot;

import java.util.Objects;

public class Regiao extends AggregateRoot<RegiaoId> {

    private final RegiaoId id;
    private NomeRegiao nome;
    private Pais pais;
    private Coordenadas coordenadas;

    private Regiao(RegiaoId id, NomeRegiao nome, Pais pais, Coordenadas coordenadas) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        this.pais = Objects.requireNonNull(pais);
        this.coordenadas = Objects.requireNonNull(coordenadas);
    }

    public static Regiao cadastrar(NomeRegiao nome, Pais pais, Coordenadas coordenadas) {
        return new Regiao(RegiaoId.novo(), nome, pais, coordenadas);
    }

    public static Regiao reconstituir(RegiaoId id, NomeRegiao nome, Pais pais, Coordenadas coordenadas) {
        return new Regiao(id, nome, pais, coordenadas);
    }

    public void atualizarCoordenadas(Coordenadas novas) {
        this.coordenadas = Objects.requireNonNull(novas);
    }

    public boolean contem(Coordenadas ponto, double raioKm) {
        if (raioKm <= 0) {
            throw new IllegalArgumentException("Raio deve ser positivo");
        }
        return coordenadas.distanciaKm(ponto) <= raioKm;
    }

    @Override
    public RegiaoId id() {
        return id;
    }

    public NomeRegiao nome() {
        return nome;
    }

    public Pais pais() {
        return pais;
    }

    public Coordenadas coordenadas() {
        return coordenadas;
    }
}
