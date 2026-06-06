package br.com.fiap.orbitasafe.eventosclimaticos.domain.service;

import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.DataOcorrencia;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.EventoClimatico;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.Intensidade;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.evento.TipoEvento;
import br.com.fiap.orbitasafe.eventosclimaticos.domain.model.observacao.ValorColetado;
import br.com.fiap.orbitasafe.geografia.domain.model.RegiaoId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.satelite.Satelite;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.SensorId;
import br.com.fiap.orbitasafe.monitoramentoespacial.domain.model.sensor.TipoSensor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ServicoDeteccaoEvento {

    public EventoClimatico detectarApartirDeObservacoes(
            TipoEvento tipo,
            RegiaoId regiaoId,
            Satelite satelite,
            List<LeituraSensor> leituras) {

        Objects.requireNonNull(tipo, "Tipo do evento e obrigatorio");
        Objects.requireNonNull(regiaoId, "Regiao do evento e obrigatoria");
        Objects.requireNonNull(satelite, "Satelite e obrigatorio");
        Objects.requireNonNull(leituras, "Leituras nao podem ser nulas");

        if (!satelite.podeColetar()) {
            throw new IllegalStateException("Satelite nao esta apto a coletar dados");
        }
        if (leituras.isEmpty()) {
            throw new IllegalArgumentException("Detectar evento exige ao menos uma leitura");
        }

        for (LeituraSensor leitura : leituras) {
            if (!satelite.possuiSensor(leitura.sensorId())) {
                throw new IllegalArgumentException(
                        "Sensor " + leitura.sensorId() + " nao pertence ao satelite informado"
                );
            }
        }

        Intensidade intensidade = calcularIntensidadeAgregada(tipo, leituras);
        DataOcorrencia dataOcorrencia = DataOcorrencia.agora();

        EventoClimatico evento = EventoClimatico.detectar(tipo, intensidade, dataOcorrencia, regiaoId);
        for (LeituraSensor leitura : leituras) {
            evento.adicionarObservacao(leitura.sensorId(), leitura.dataColeta(), leitura.valor());
        }
        return evento;
    }

    private Intensidade calcularIntensidadeAgregada(TipoEvento tipo, List<LeituraSensor> leituras) {
        BigDecimal soma = leituras.stream()
                .map(l -> l.valor().valor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal media = soma.divide(BigDecimal.valueOf(leituras.size()), java.math.RoundingMode.HALF_UP);

        BigDecimal ajustada = switch (tipo) {
            case FURACAO, TORNADO -> media.multiply(new BigDecimal("1.20"));
            case ENCHENTE, TEMPESTADE -> media.multiply(new BigDecimal("1.10"));
            case SECA, ONDA_DE_CALOR -> media.multiply(new BigDecimal("1.05"));
            default -> media;
        };
        BigDecimal limitada = ajustada.min(new BigDecimal("100"));
        return Intensidade.de(limitada.doubleValue());
    }

    public record LeituraSensor(
            SensorId sensorId,
            TipoSensor tipoSensor,
            LocalDateTime dataColeta,
            ValorColetado valor
    ) {
        public LeituraSensor {
            Objects.requireNonNull(sensorId);
            Objects.requireNonNull(tipoSensor);
            Objects.requireNonNull(dataColeta);
            Objects.requireNonNull(valor);
        }
    }
}
