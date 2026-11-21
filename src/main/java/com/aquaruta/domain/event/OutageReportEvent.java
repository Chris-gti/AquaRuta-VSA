package com.aquaruta.domain.event;

import com.aquaruta.domain.model.CitizenReport;

import java.time.Instant;
import java.util.Objects;

/**
 * Evento de dominio disparado al registrarse un nuevo reporte ciudadano en el sistema.
 *
 * @param report    El reporte ciudadano ingresado
 * @param timestamp Instante temporal de la emisión del evento
 *
 * @author AquaRuta VSA Engineering Team
 */
public record OutageReportEvent(CitizenReport report, Instant timestamp) {

    public OutageReportEvent(CitizenReport report) {
        this(Objects.requireNonNull(report, "El reporte no puede ser nulo"), Instant.now());
    }
}
