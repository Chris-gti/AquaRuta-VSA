package com.aquaruta.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio que modela un tramo o segmento de calle (arista de la red vial).
 * <p>
 * Implementa propiedades viales y factores de riesgo hidráulico calculados
 * dinámicamente mediante el patrón Strategy.
 * </p>
 *
 * @author AquaRuta VSA Engineering Team
 */
public class RoadEdge {

    private final String id;
    private final Long sourceNodeId;
    private final Long targetNodeId;
    private final String streetName;
    private final String highwayType;
    private final double lengthMeters;
    private final boolean oneway;
    private final int maxspeed;
    private double baseRiskFactor;
    private double currentDynamicWeight;
    private boolean blocked;

    public RoadEdge(String id,
                    Long sourceNodeId,
                    Long targetNodeId,
                    String streetName,
                    String highwayType,
                    double lengthMeters,
                    boolean oneway,
                    int maxspeed,
                    double baseRiskFactor) {
        this.id = Objects.requireNonNull(id, "El ID del tramo no puede ser nulo");
        this.sourceNodeId = Objects.requireNonNull(sourceNodeId, "sourceNodeId no puede ser nulo");
        this.targetNodeId = Objects.requireNonNull(targetNodeId, "targetNodeId no puede ser nulo");
        this.streetName = streetName != null && !streetName.isBlank() ? streetName : "Sin Nombre";
        this.highwayType = highwayType != null ? highwayType : "unclassified";
        this.lengthMeters = lengthMeters > 0 ? lengthMeters : 1.0;
        this.oneway = oneway;
        this.maxspeed = maxspeed > 0 ? maxspeed : 40;
        this.baseRiskFactor = baseRiskFactor > 0 ? baseRiskFactor : 1.0;
        this.currentDynamicWeight = this.lengthMeters * this.baseRiskFactor;
        this.blocked = false;
    }

    public String getId() {
        return id;
    }

    public Long getSourceNodeId() {
        return sourceNodeId;
    }

    public Long getTargetNodeId() {
        return targetNodeId;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getHighwayType() {
        return highwayType;
    }

    public double getLengthMeters() {
        return lengthMeters;
    }

    public boolean isOneway() {
        return oneway;
    }

    public int getMaxspeed() {
        return maxspeed;
    }

    public double getBaseRiskFactor() {
        return baseRiskFactor;
    }

    public void setBaseRiskFactor(double baseRiskFactor) {
        this.baseRiskFactor = baseRiskFactor;
    }

    public double getCurrentDynamicWeight() {
        return currentDynamicWeight;
    }

    public void setCurrentDynamicWeight(double currentDynamicWeight) {
        this.currentDynamicWeight = currentDynamicWeight;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoadEdge roadEdge = (RoadEdge) o;
        return Objects.equals(id, roadEdge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RoadEdge{" +
                "id='" + id + '\'' +
                ", street='" + streetName + '\'' +
                ", (" + sourceNodeId + " -> " + targetNodeId + ")" +
                ", length=" + lengthMeters + "m" +
                ", weight=" + currentDynamicWeight +
                ", blocked=" + blocked + "}";
    }
}
