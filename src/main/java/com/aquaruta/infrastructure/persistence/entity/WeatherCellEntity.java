package com.aquaruta.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Polygon;

import java.time.Instant;

/**
 * Entidad JPA para cuadrantes meteorológicos en PostGIS.
 */
@Entity
@Table(name = "weather_cells")
public class WeatherCellEntity {

    @Id
    @Column(length = 32)
    private String id;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(name = "center_lat", nullable = false)
    private Double centerLat;

    @Column(name = "center_lon", nullable = false)
    private Double centerLon;

    @Column(name = "precipitation_mm_h", nullable = false)
    private Double precipitationMmH;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Column(nullable = false, columnDefinition = "geometry(Polygon, 4326)")
    private Polygon geom;

    public WeatherCellEntity() {
    }

    public WeatherCellEntity(String id, String name, Double centerLat, Double centerLon,
                             Double precipitationMmH, Instant lastUpdated, Polygon geom) {
        this.id = id;
        this.name = name;
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.precipitationMmH = precipitationMmH;
        this.lastUpdated = lastUpdated;
        this.geom = geom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(Double centerLat) {
        this.centerLat = centerLat;
    }

    public Double getCenterLon() {
        return centerLon;
    }

    public void setCenterLon(Double centerLon) {
        this.centerLon = centerLon;
    }

    public Double getPrecipitationMmH() {
        return precipitationMmH;
    }

    public void setPrecipitationMmH(Double precipitationMmH) {
        this.precipitationMmH = precipitationMmH;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Polygon getGeom() {
        return geom;
    }

    public void setGeom(Polygon geom) {
        this.geom = geom;
    }
}
