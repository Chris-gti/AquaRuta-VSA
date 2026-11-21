package com.aquaruta.infrastructure.event;

import com.aquaruta.domain.event.OutageClusterConfirmedEvent;
import com.aquaruta.domain.event.OutageEventPublisherPort;
import com.aquaruta.domain.event.OutageReportEvent;
import com.aquaruta.domain.model.CitizenReport;
import com.aquaruta.domain.model.ReportStatus;
import com.aquaruta.domain.model.ReportType;
import com.aquaruta.domain.model.ValidatedAlert;
import com.aquaruta.domain.repository.CitizenReportRepositoryPort;
import com.aquaruta.domain.repository.ValidatedAlertRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class OutageConsensusHandlerTest {

    private List<CitizenReport> storedReports;
    private List<ValidatedAlert> storedAlerts;
    private AtomicBoolean clusterEventFired;
    private OutageConsensusHandler consensusHandler;

    @BeforeEach
    void setUp() {
        storedReports = new ArrayList<>();
        storedAlerts = new ArrayList<>();
        clusterEventFired = new AtomicBoolean(false);

        CitizenReportRepositoryPort fakeReportRepo = new CitizenReportRepositoryPort() {
            @Override
            public CitizenReport save(CitizenReport report) {
                storedReports.add(report);
                return report;
            }

            @Override
            public Optional<CitizenReport> findById(String id) {
                return storedReports.stream().filter(r -> r.getId().equals(id)).findFirst();
            }

            @Override
            public List<CitizenReport> findAll() {
                return storedReports;
            }

            @Override
            public List<CitizenReport> findRecentActiveReports(ReportType type, Instant since) {
                return storedReports.stream()
                        .filter(r -> r.getReportType() == type && !r.getCreatedAt().isBefore(since))
                        .toList();
            }

            @Override
            public List<CitizenReport> findNearbyReports(double latitude, double longitude, double radiusMeters, ReportType type, Instant since) {
                return storedReports;
            }

            @Override
            public void updateStatus(String reportId, ReportStatus status, String clusterId) {
                for (CitizenReport r : storedReports) {
                    if (r.getId().equals(reportId)) {
                        r.setStatus(status);
                        r.setClusterId(clusterId);
                    }
                }
            }
        };

        ValidatedAlertRepositoryPort fakeAlertRepo = new ValidatedAlertRepositoryPort() {
            @Override
            public ValidatedAlert save(ValidatedAlert alert) {
                storedAlerts.add(alert);
                return alert;
            }

            @Override
            public Optional<ValidatedAlert> findById(String id) {
                return storedAlerts.stream().filter(a -> a.getId().equals(id)).findFirst();
            }

            @Override
            public List<ValidatedAlert> findAllActive() {
                return storedAlerts;
            }

            @Override
            public List<ValidatedAlert> findActiveNearby(double latitude, double longitude, double radiusMeters, ReportType type) {
                return storedAlerts;
            }
        };

        OutageEventPublisherPort fakePublisher = new OutageEventPublisherPort() {
            @Override
            public void publishReportReceived(OutageReportEvent event) {}

            @Override
            public void publishClusterConfirmed(OutageClusterConfirmedEvent event) {
                clusterEventFired.set(true);
            }
        };

        // Radio: 200m, Quórum: 3 reportes, Ventana: 2 horas
        consensusHandler = new OutageConsensusHandler(fakeReportRepo, fakeAlertRepo, fakePublisher, 200.0, 3, 2);
    }

    @Test
    @DisplayName("Con 3 reportes en un radio menor a 200m, se debe alcanzar quórum y generar ValidatedAlert")
    void testConsensusQuorumReached() {
        // Reporte 1: Parque Juárez (Centro)
        CitizenReport r1 = CitizenReport.createNew(ReportType.CORTE_AGUA, "Sin agua desde las 2pm", 17.9890, -92.9250);
        storedReports.add(r1);
        consensusHandler.handleNewReport(new OutageReportEvent(r1));
        assertFalse(clusterEventFired.get(), "Con 1 reporte no debe disparar alerta confirmada");

        // Reporte 2: A 80 metros de distancia
        CitizenReport r2 = CitizenReport.createNew(ReportType.CORTE_AGUA, "Tampoco tenemos agua en la esquina", 17.9896, -92.9252);
        storedReports.add(r2);
        consensusHandler.handleNewReport(new OutageReportEvent(r2));
        assertFalse(clusterEventFired.get(), "Con 2 reportes no debe disparar alerta confirmada");

        // Reporte 3: A 120 metros de distancia
        CitizenReport r3 = CitizenReport.createNew(ReportType.CORTE_AGUA, "Se fue la presión por completo", 17.9885, -92.9248);
        storedReports.add(r3);
        consensusHandler.handleNewReport(new OutageReportEvent(r3));

        // Debe haber alcanzado el quórum
        assertTrue(clusterEventFired.get(), "Con 3 reportes a < 200m debe disparar alerta confirmada");
        assertEquals(1, storedAlerts.size(), "Debe existir exactamente 1 alerta validada en BD");
        assertEquals(3, storedAlerts.get(0).getReportCount());
        assertEquals(ReportType.CORTE_AGUA, storedAlerts.get(0).getAlertType());
    }
}
