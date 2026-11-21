package com.aquaruta.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio que representa un nodo (intersección o vértice) de la red vial.
 * <p>
 * Contiene coordenadas geográficas y su altitud sobre el nivel medio del mar (m.s.n.m.),
 * métrica indispensable para el análisis hidráulico de anegamiento en Villahermosa.
 * </p>
 *
 * @author AquaRuta VSA Engineering Team
 */
public class RoadNode {

    private final Long id;
    private final double latitude;
    private final double longitude;
    private double elevationMsnm;

    /**
     * Constructor principal del nodo vial.
     *
     * @param id            Identificador único OSM (OpenStreetMap Node ID)
     * @param latitude      Latitud en grados decimales
     * @param longitude     Longitud en grados decimales
     * @param elevationMsnm Elevación topográfica en metros sobre el nivel del mar
     */
    public RoadNode(Long id, double latitude, double longitude, double elevationMsnm) {
        this.id = Objects.requireNonNull(id, "El ID del nodo no puede ser nulo");
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevationMsnm = elevationMsnm;
    }

    public Long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getElevationMsnm() {
        return elevationMsnm;
    }

    public void setElevationMsnm(double elevationMsnm) {
        this.elevationMsnm = elevationMsnm;
    }

    public GeoPoint toGeoPoint() {
        return new GeoPoint(latitude, longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoadNode roadNode = (RoadNode) o;
        return Objects.equals(id, roadNode.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RoadNode{" +
                "id=" + id +
                ", lat=" + latitude +
                ", lon=" + longitude +
                ", elevation=" + elevationMsnm + " msnm}";
    }
}
