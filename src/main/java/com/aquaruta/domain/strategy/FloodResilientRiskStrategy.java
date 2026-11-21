package com.aquaruta.domain.strategy;

import com.aquaruta.domain.model.RoadEdge;
import com.aquaruta.domain.model.RoadNode;

/**
 * Estrategia de cálculo de riesgo hídrico y resiliencia vial para Villahermosa.
 * <p>
 * Modela la vulnerabilidad topográfica de zonas bajas (como Gaviotas, Centro y Ruiz Cortines),
 * combinando la altitud del nodo, la intensidad de lluvia en mm/h y los reportes ciudadanos
 * de anegamiento para penalizar o bloquear aristas en el grafo.
 * </p>
 *
 * @author AquaRuta VSA Engineering Team
 */
public class FloodResilientRiskStrategy implements RiskCalculationStrategy {

    private static final double BLOCKED_PENALTY_WEIGHT = 1_000_000.0;
    private static final double VILLAHERMOSA_CRITICAL_ELEVATION_MSNM = 6.5;

    @Override
    public double calculateWeight(RoadEdge edge,
                                  RoadNode targetNode,
                                  double localPrecipitationMmH,
                                  int activeFloodReportsNearby) {
        // Si el tramo ya fue marcado como bloqueado o tiene 3+ reportes confirmados de anegamiento
        if (edge.isBlocked() || activeFloodReportsNearby >= 3) {
            return BLOCKED_PENALTY_WEIGHT;
        }

        double speedMps = (edge.getMaxspeed() * 1000.0) / 3600.0;
        double baseTimeSeconds = edge.getLengthMeters() / Math.max(5.0, speedMps);

        // 1. Penalización topográfica por cota baja (m.s.n.m.)
        double elevation = targetNode != null ? targetNode.getElevationMsnm() : 8.0;
        double elevationPenalty = 1.0;
        if (elevation < VILLAHERMOSA_CRITICAL_ELEVATION_MSNM) {
            // A menor cota, mayor riesgo exponencial de acumulación de escorrentía
            double delta = VILLAHERMOSA_CRITICAL_ELEVATION_MSNM - elevation;
            elevationPenalty = 1.0 + Math.pow(delta, 1.6);
        }

        // 2. Factor de precipitación pluvial actual (mm/h)
        double rainPenalty = 1.0;
        if (localPrecipitationMmH > 0.0) {
            // Lluvias de más de 30 mm/h causan encharcamientos severos en Villahermosa
            double rainIntensity = localPrecipitationMmH / 20.0;
            rainPenalty = 1.0 + (Math.pow(rainIntensity, 1.7) * elevationPenalty);
        }

        // 3. Penalización por reportes ciudadanos activos en la proximidad (1 o 2 reportes no confirmados)
        double reportPenalty = 1.0 + (activeFloodReportsNearby * 4.5);

        // 4. Si la combinación de lluvia fuerte (>50 mm/h) y cota muy baja (<4.5m) satura el tramo
        if (localPrecipitationMmH >= 50.0 && elevation < 4.5) {
            return BLOCKED_PENALTY_WEIGHT * 0.5; // Prácticamente intransitable
        }

        return baseTimeSeconds * elevationPenalty * rainPenalty * reportPenalty;
    }

    @Override
    public String getStrategyName() {
        return "FLOOD_RESILIENT_RISK_STRATEGY";
    }
}
