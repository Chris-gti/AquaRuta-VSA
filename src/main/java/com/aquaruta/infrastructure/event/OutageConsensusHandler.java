package com.aquaruta.infrastructure.event;

import com.aquaruta.domain.event.OutageClusterConfirmedEvent;
import com.aquaruta.domain.event.OutageEventPublisherPort;
import com.aquaruta.domain.event.OutageReportEvent;
import com.aquaruta.domain.model.CitizenReport;
import com.aquaruta.domain.model.GeoPoint;
import com.aquaruta.domain.model.ReportStatus;
import com.aquaruta.domain.model.ReportType;
import com.aquaruta.domain.model.ValidatedAlert;
import com.aquaruta.domain.repository.CitizenReportRepositoryPort;
import com.aquaruta.domain.repository.ValidatedAlertRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Gestor del consenso espacio-temporal para la validación de reportes ciudadanos.
 * <p>
 * Implementa el patrón Observer escuchando {@link OutageReportEvent}. Aplica la fórmula
 * de Haversine para evaluar si se cumple el quórum de al menos 3 reportes en un radio
 * de 200 metros dentro de una ventana de 2 horas. Al confirmarse, eleva el estado a
 * {@link ValidatedAlert} y emite {@link OutageClusterConfirmedEvent}.
 * </p>
 *
 * @author AquaRuta VSA Engineering Team
 */
@Service
public class OutageConsensusHandler {

    private static final Logger log = LoggerFactory.getLogger(OutageConsensusHandler.class);

    private final CitizenReportRepositoryPort reportRepository;
    private final ValidatedAlertRepositoryPort alertRepository;
    private final OutageEventPublisherPort publisherPort;

    private final double quorumRadiusMeters;
    private final int quorumCount;
    private final int windowHours;

    public OutageConsensusHandler(
            CitizenReportRepositoryPort reportRepository,
            ValidatedAlertRepositoryPort alertRepository,
            OutageEventPublisherPort publisherPort,
            @Value("${aquaruta.consensus.radius-meters:200.0}") double quorumRadiusMeters,
            @Value("${aquaruta.consensus.quorum-count:3}") int quorumCount,
            @Value("${aquaruta.consensus.window-hours:2}") int windowHours) {
        this.reportRepository = reportRepository;
        this.alertRepository = alertRepository;
        this.publisherPort = publisherPort;
        this.quorumRadiusMeters = quorumRadiusMeters;
        this.quorumCount = quorumCount;
        this.windowHours = windowHours;
    }

    /**
     * Escucha el evento de nuevo reporte y ejecuta el algoritmo de agrupamiento (Clustering).
     *
     * @param event Evento con el reporte recién ingresado
     */
    @EventListener
    public void handleNewReport(OutageReportEvent event) {
        CitizenReport incoming = event.report();
        log.info("Procesando consenso para nuevo reporte: ID={}, Tipo={}, Coord=({}, {})",
                incoming.getId(), incoming.getReportType(), incoming.getLatitude(), incoming.getLongitude());

        Instant windowThreshold = Instant.now().minus(Duration.ofHours(windowHours));
        List<CitizenReport> recentReports = reportRepository.findRecentActiveReports(incoming.getReportType(), windowThreshold);

        GeoPoint targetPoint = incoming.toGeoPoint();
        List<CitizenReport> clusteredReports = new ArrayList<>();

        for (CitizenReport candidate : recentReports) {
            double distance = targetPoint.distanceTo(candidate.toGeoPoint());
            if (distance <= quorumRadiusMeters) {
                clusteredReports.add(candidate);
            }
        }

        // Si el reporte entrante aún no estaba en la lista de persistidos
        if (clusteredReports.stream().noneMatch(r -> r.getId().equals(incoming.getId()))) {
            clusteredReports.add(incoming);
        }

        log.debug("Encontrados {} reportes en radio de {}m para tipo {}",
                clusteredReports.size(), quorumRadiusMeters, incoming.getReportType());

        if (clusteredReports.size() >= quorumCount) {
            log.info("¡Quórum de consenso alcanzado! ({} reportes >= {} requeridos)",
                    clusteredReports.size(), quorumCount);

            // Calcular centroide ponderado de los reportes
            double sumLat = 0.0;
            double sumLon = 0.0;
            for (CitizenReport r : clusteredReports) {
                sumLat += r.getLatitude();
                sumLon += r.getLongitude();
            }
            double centroidLat = sumLat / clusteredReports.size();
            double centroidLon = sumLon / clusteredReports.size();

            String clusterId = UUID.randomUUID().toString();
            ValidatedAlert alert = new ValidatedAlert(
                    clusterId,
                    incoming.getReportType(),
                    centroidLat,
                    centroidLon,
                    quorumRadiusMeters,
                    clusteredReports.size(),
                    Instant.now(),
                    "ACTIVA"
            );

            // Persistir la alerta validada
            alertRepository.save(alert);

            // Actualizar el estado de cada reporte agrupado
            for (CitizenReport r : clusteredReports) {
                r.setStatus(ReportStatus.CONFIRMADO);
                r.setClusterId(clusterId);
                reportRepository.updateStatus(r.getId(), ReportStatus.CONFIRMADO, clusterId);
            }

            // Notificar a los observadores sobre la confirmación de la alerta
            publisherPort.publishClusterConfirmed(new OutageClusterConfirmedEvent(alert));
        }
    }
}
