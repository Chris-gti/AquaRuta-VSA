package com.aquaruta.infrastructure.event;

import com.aquaruta.domain.event.OutageClusterConfirmedEvent;
import com.aquaruta.domain.event.OutageEventPublisherPort;
import com.aquaruta.domain.event.OutageReportEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Adaptador de infraestructura para la publicación de eventos de dominio
 * utilizando el bus reactivo de eventos de Spring ApplicationContext.
 *
 * @author AquaRuta VSA Engineering Team
 */
@Component
public class SpringOutageEventPublisher implements OutageEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(SpringOutageEventPublisher.class);

    private final ApplicationEventPublisher eventPublisher;

    public SpringOutageEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher no puede ser nulo");
    }

    @Override
    public void publishReportReceived(OutageReportEvent event) {
        log.info("Disparando evento de reporte ciudadano recibido: ID={}, Tipo={}",
                event.report().getId(), event.report().getReportType());
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publishClusterConfirmed(OutageClusterConfirmedEvent event) {
        log.warn("¡ALERTA CONFIRMADA POR QUÓRUM! Tipo={}, Centroide=({}, {}), Reportes={}",
                event.alert().getAlertType(),
                event.alert().getCentroidLat(),
                event.alert().getCentroidLon(),
                event.alert().getReportCount());
        eventPublisher.publishEvent(event);
    }
}
