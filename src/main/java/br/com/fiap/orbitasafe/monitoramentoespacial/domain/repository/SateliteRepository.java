package br.com.fiap.orbitasafe.monitoramentoespacial.domain.repository;

import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.NomeSatelite;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.Satelite;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.SateliteId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.SensorId;
import br.com.fiap.orbitasafe.shared.domain.Repository;

import java.util.List;
import java.util.Optional;

public interface SateliteRepository extends Repository<Satelite, SateliteId> {

    Optional<Satelite> buscarPorNome(NomeSatelite nome);

    Optional<Satelite> buscarPorSensor(SensorId sensorId);

    List<Satelite> listarAtivos();
}
