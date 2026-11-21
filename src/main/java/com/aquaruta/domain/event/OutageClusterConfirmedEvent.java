package com.aquaruta.domain.event;

import com.aquaruta.domain.model.ValidatedAlert;

import java.time.Instant;
import java.util.Objects;

/**
 * Evento de dominio emitido cuando un grupo de reportes alcanza el quórum
 * de consenso espacial (radio Haversine &lt; 200m) y temporal (ventana de 2h).
 *
 * @param alert     La alerta validada y confirmada
 * @param timestamp Instante temporal de la confirmación
 *
 * @author AquaRuta VSA Engineering Team
 */
public record OutageClusterConfirmedEvent(ValidatedAlert alert, Instant timestamp) {

    public OutageClusterConfirmedEvent(ValidatedAlert alert) {
        this(Objects.requireNonNull(alert, "La alerta confirmada no puede ser nula"), Instant.now());
    }
}
