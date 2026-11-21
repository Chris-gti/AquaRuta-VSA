package com.aquaruta.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio que representa un reporte ciudadano geolocalizado.
 *
 * @author AquaRuta VSA Engineering Team
 */
public class CitizenReport {

    private final String id;
    private final ReportType reportType;
    private final String description;
    private final double latitude;
    private final double longitude;
    private final Instant createdAt;
    private ReportStatus status;
    private String clusterId;

    public CitizenReport(String id,
                         ReportType reportType,
                         String description,
                         double latitude,
                         double longitude,
                         Instant createdAt,
                         ReportStatus status,
                         String clusterId) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.reportType = Objects.requireNonNull(reportType, "reportType no puede ser nulo");
        this.description = description != null ? description : "";
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.status = status != null ? status : ReportStatus.PENDIENTE_VERIFICACION;
        this.clusterId = clusterId;
    }

    public static CitizenReport createNew(ReportType reportType, String description, double lat, double lon) {
        return new CitizenReport(
                UUID.randomUUID().toString(),
                reportType,
                description,
                lat,
                lon,
                Instant.now(),
                ReportStatus.PENDIENTE_VERIFICACION,
                null
        );
    }

    public String getId() {
        return id;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public GeoPoint toGeoPoint() {
        return new GeoPoint(latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CitizenReport that = (CitizenReport) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
