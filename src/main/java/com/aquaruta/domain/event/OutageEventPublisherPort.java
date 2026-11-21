package com.aquaruta.domain.event;

/**
 * Puerto de salida para la publicación de eventos de corte hídrico y anegamientos.
 * Desacopla el núcleo de dominio de la infraestructura de mensajería (Spring ApplicationEventPublisher, WebSockets, etc.).
 *
 * @author AquaRuta VSA Engineering Team
 */
public interface OutageEventPublisherPort {

    /**
     * Publica la recepción de un nuevo reporte ciudadano individual.
     *
     * @param event Evento del reporte
     */
    void publishReportReceived(OutageReportEvent event);

    /**
     * Publica la confirmación de una alerta validada por consenso.
     *
     * @param event Evento de la alerta confirmada
     */
    void publishClusterConfirmed(OutageClusterConfirmedEvent event);
}
