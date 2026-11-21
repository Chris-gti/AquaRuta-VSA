package com.aquaruta.infrastructure.persistence.entity;

import com.aquaruta.domain.model.ReportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Point;

import java.time.Instant;

/**
 * Entidad JPA para alertas consolidadas por quórum en PostGIS.
 */
@Entity
@Table(name = "validated_alerts")
public class ValidatedAlertEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 32)
    private ReportType alertType;

    @Column(name = "centroid_lat", nullable = false)
    private Double centroidLat;

    @Column(name = "centroid_lon", nullable = false)
    private Double centroidLon;

    @Column(name = "radius_meters", nullable = false)
    private Double radiusMeters;

    @Column(name = "report_count", nullable = false)
    private Integer reportCount;

    @Column(name = "confirmed_at", nullable = false)
    private Instant confirmedAt;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "centroid_geom", nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point centroidGeom;

    public ValidatedAlertEntity() {
    }

    public ValidatedAlertEntity(String id, ReportType alertType, Double centroidLat, Double centroidLon,
                                Double radiusMeters, Integer reportCount, Instant confirmedAt,
                                String status, Point centroidGeom) {
        this.id = id;
        this.alertType = alertType;
        this.centroidLat = centroidLat;
        this.centroidLon = centroidLon;
        this.radiusMeters = radiusMeters;
        this.reportCount = reportCount;
        this.confirmedAt = confirmedAt;
        this.status = status;
        this.centroidGeom = centroidGeom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReportType getAlertType() {
        return alertType;
    }

    public void setAlertType(ReportType alertType) {
        this.alertType = alertType;
    }

    public Double getCentroidLat() {
        return centroidLat;
    }

    public void setCentroidLat(Double centroidLat) {
        this.centroidLat = centroidLat;
    }

    public Double getCentroidLon() {
        return centroidLon;
    }

    public void setCentroidLon(Double centroidLon) {
        this.centroidLon = centroidLon;
    }

    public Double getRadiusMeters() {
        return radiusMeters;
    }

    public void setRadiusMeters(Double radiusMeters) {
        this.radiusMeters = radiusMeters;
    }

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public Instant getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Instant confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Point getCentroidGeom() {
        return centroidGeom;
    }

    public void setCentroidGeom(Point centroidGeom) {
        this.centroidGeom = centroidGeom;
    }
}
