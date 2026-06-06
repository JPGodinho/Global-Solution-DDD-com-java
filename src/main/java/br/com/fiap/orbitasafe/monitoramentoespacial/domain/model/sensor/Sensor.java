package br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor;

import br.com.fiap.orbitasafe.shared.domain.Entity;

import java.util.Objects;

public class Sensor extends Entity<SensorId> {

    private final SensorId id;
    private final String nome;
    private final TipoSensor tipo;
    private Precisao precisao;

    private Sensor(SensorId id, String nome, TipoSensor tipo, Precisao precisao) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome do sensor nao pode ser vazio");
        }
        this.tipo = Objects.requireNonNull(tipo);
        this.precisao = Objects.requireNonNull(precisao);
    }

    public static Sensor instalar(String nome, TipoSensor tipo, Precisao precisao) {
        return new Sensor(SensorId.novo(), nome, tipo, precisao);
    }

    public static Sensor reconstituir(SensorId id, String nome, TipoSensor tipo, Precisao precisao) {
        return new Sensor(id, nome, tipo, precisao);
    }

    public void recalibrar(Precisao novaPrecisao) {
        this.precisao = Objects.requireNonNull(novaPrecisao);
    }

    @Override
    public SensorId id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public TipoSensor tipo() {
        return tipo;
    }

    public Precisao precisao() {
        return precisao;
    }
}
