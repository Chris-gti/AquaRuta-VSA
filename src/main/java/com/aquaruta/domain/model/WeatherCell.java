package com.aquaruta.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidad de dominio que representa una célula o cuadrante meteorológico
 * dentro de la malla urbana de Villahermosa Centro.
 *
 * @author AquaRuta VSA Engineering Team
 */
public class WeatherCell {

    private final String id;
    private final String name;
    private final double centerLat;
    private final double centerLon;
    private double precipitationMmH;
    private Instant lastUpdated;

    public WeatherCell(String id, String name, double centerLat, double centerLon, double precipitationMmH, Instant lastUpdated) {
        this.id = Objects.requireNonNull(id, "El ID de la célula meteorológica no puede ser nulo");
        this.name = Objects.requireNonNull(name, "El nombre de la célula no puede ser nulo");
        this.centerLat = centerLat;
        this.centerLon = centerLon;
        this.precipitationMmH = Math.max(0.0, precipitationMmH);
        this.lastUpdated = lastUpdated != null ? lastUpdated : Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public double getCenterLon() {
        return centerLon;
    }

    public double getPrecipitationMmH() {
        return precipitationMmH;
    }

    public void setPrecipitationMmH(double precipitationMmH) {
        this.precipitationMmH = Math.max(0.0, precipitationMmH);
        this.lastUpdated = Instant.now();
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public GeoPoint toCenterGeoPoint() {
        return new GeoPoint(centerLat, centerLon);
    }
}
