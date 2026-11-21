package com.aquaruta.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio que representa una zona de alerta confirmada
 * mediante consenso espacio-temporal ciudadano (Haversine &lt; 200m y ventana temporal).
 *
 * @author AquaRuta VSA Engineering Team
 */
public class ValidatedAlert {

    private final String id;
    private final ReportType alertType;
    private final double centroidLat;
    private final double centroidLon;
    private final double radiusMeters;
    private int reportCount;
    private final Instant confirmedAt;
    private String status;

    public ValidatedAlert(String id,
                          ReportType alertType,
                          double centroidLat,
                          double centroidLon,
                          double radiusMeters,
                          int reportCount,
                          Instant confirmedAt,
                          String status) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.alertType = Objects.requireNonNull(alertType, "alertType no puede ser nulo");
        this.centroidLat = centroidLat;
        this.centroidLon = centroidLon;
        this.radiusMeters = radiusMeters > 0 ? radiusMeters : 200.0;
        this.reportCount = reportCount;
        this.confirmedAt = confirmedAt != null ? confirmedAt : Instant.now();
        this.status = status != null ? status : "ACTIVA";
    }

    public String getId() {
        return id;
    }

    public ReportType getAlertType() {
        return alertType;
    }

    public double getCentroidLat() {
        return centroidLat;
    }

    public double getCentroidLon() {
        return centroidLon;
    }

    public double getRadiusMeters() {
        return radiusMeters;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void incrementReportCount() {
        this.reportCount++;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GeoPoint toCentroidGeoPoint() {
        return new GeoPoint(centroidLat, centroidLon);
    }
}
