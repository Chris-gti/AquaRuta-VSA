package com.aquaruta.domain.strategy;

import com.aquaruta.domain.model.RoadEdge;
import com.aquaruta.domain.model.RoadNode;

/**
 * Estrategia de costo estándar en condiciones climáticas secas.
 * Considera la distancia física y la jerarquía de la vialidad (velocidad máxima).
 *
 * @author AquaRuta VSA Engineering Team
 */
public class NormalWeatherRiskStrategy implements RiskCalculationStrategy {

    @Override
    public double calculateWeight(RoadEdge edge,
                                  RoadNode targetNode,
                                  double localPrecipitationMmH,
                                  int activeFloodReportsNearby) {
        if (edge.isBlocked()) {
            return 1_000_000.0;
        }

        // Costo base proporcional al tiempo de recorrido ideal (segundos)
        double speedMps = (edge.getMaxspeed() * 1000.0) / 3600.0;
        double baseTimeSeconds = edge.getLengthMeters() / Math.max(5.0, speedMps);

        // Bonificación por tipo de vía jerárquica (autopistas / primarias son más fluidas)
        double hierarchyMultiplier = switch (edge.getHighwayType()) {
            case "primary", "trunk" -> 0.9;
            case "secondary" -> 1.0;
            case "tertiary" -> 1.1;
            case "residential" -> 1.25;
            default -> 1.35;
        };

        return baseTimeSeconds * hierarchyMultiplier * edge.getBaseRiskFactor();
    }

    @Override
    public String getStrategyName() {
        return "NORMAL_WEATHER_STRATEGY";
    }
}
