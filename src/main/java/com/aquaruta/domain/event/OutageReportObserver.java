package com.aquaruta.domain.event;

/**
 * Interfaz Observer para componentes interesados en reaccionar a cambios
 * en el estado de reportes ciudadanos y alertas hídricas.
 *
 * @author AquaRuta VSA Engineering Team
 */
public interface OutageReportObserver {

    /**
     * Invocado cuando se recibe un reporte.
     *
     * @param event Evento con la información del reporte
     */
    void onReportReceived(OutageReportEvent event);

    /**
     * Invocado cuando un grupo de reportes alcanza el quórum y se confirma la alerta.
     *
     * @param event Evento con la alerta confirmada
     */
    void onClusterConfirmed(OutageClusterConfirmedEvent event);
}
