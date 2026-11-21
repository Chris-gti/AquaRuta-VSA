package com.aquaruta.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Point;

/**
 * Entidad JPA para persistencia de nodos viales en PostGIS.
 */
@Entity
@Table(name = "road_nodes")
public class RoadNodeEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "elevation_msnm", nullable = false)
    private Double elevationMsnm;

    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point geom;

    public RoadNodeEntity() {
    }

    public RoadNodeEntity(Long id, Double latitude, Double longitude, Double elevationMsnm, Point geom) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevationMsnm = elevationMsnm;
        this.geom = geom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getElevationMsnm() {
        return elevationMsnm;
    }

    public void setElevationMsnm(Double elevationMsnm) {
        this.elevationMsnm = elevationMsnm;
    }

    public Point getGeom() {
        return geom;
    }

    public void setGeom(Point geom) {
        this.geom = geom;
    }
}
