package com.aquaruta.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.LineString;

/**
 * Entidad JPA para persistencia de tramos viales en PostGIS.
 */
@Entity
@Table(name = "road_edges")
public class RoadEdgeEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "source_node_id", nullable = false)
    private Long sourceNodeId;

    @Column(name = "target_node_id", nullable = false)
    private Long targetNodeId;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "highway_type", length = 64)
    private String highwayType;

    @Column(name = "length_meters", nullable = false)
    private Double lengthMeters;

    private Boolean oneway;

    private Integer maxspeed;

    @Column(name = "base_risk_factor")
    private Double baseRiskFactor;

    @Column(nullable = false, columnDefinition = "geometry(LineString, 4326)")
    private LineString geom;

    public RoadEdgeEntity() {
    }

    public RoadEdgeEntity(String id, Long sourceNodeId, Long targetNodeId, String streetName,
                          String highwayType, Double lengthMeters, Boolean oneway,
                          Integer maxspeed, Double baseRiskFactor, LineString geom) {
        this.id = id;
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.streetName = streetName;
        this.highwayType = highwayType;
        this.lengthMeters = lengthMeters;
        this.oneway = oneway;
        this.maxspeed = maxspeed;
        this.baseRiskFactor = baseRiskFactor;
        this.geom = geom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNodeId(Long sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public Long getTargetNodeId() {
        return targetNodeId;
    }

    public void setTargetNodeId(Long targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHighwayType() {
        return highwayType;
    }

    public void setHighwayType(String highwayType) {
        this.highwayType = highwayType;
    }

    public Double getLengthMeters() {
        return lengthMeters;
    }

    public void setLengthMeters(Double lengthMeters) {
        this.lengthMeters = lengthMeters;
    }

    public Boolean getOneway() {
        return oneway;
    }

    public void setOneway(Boolean oneway) {
        this.oneway = oneway;
    }

    public Integer getMaxspeed() {
        return maxspeed;
    }

    public void setMaxspeed(Integer maxspeed) {
        this.maxspeed = maxspeed;
    }

    public Double getBaseRiskFactor() {
        return baseRiskFactor;
    }

    public void setBaseRiskFactor(Double baseRiskFactor) {
        this.baseRiskFactor = baseRiskFactor;
    }

    public LineString getGeom() {
        return geom;
    }

    public void setGeom(LineString geom) {
        this.geom = geom;
    }
}
