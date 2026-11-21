package com.aquaruta.infrastructure.graph;

import com.aquaruta.domain.model.RoadNode;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;

import java.util.Map;

/**
 * Heurística admisible y consistente para el algoritmo A* sobre la red vial de Villahermosa.
 * <p>
 * Calcula la distancia ortodrómica (Haversine) entre el nodo actual y el nodo objetivo,
 * dividiéndola por la velocidad máxima posible en la ciudad (80 km/h = ~22.22 m/s).
 * Al subestimar consistentemente el costo real de viaje, garantiza la admisibilidad
 * matemática de A* y la obtención de la ruta óptima.
 * </p>
 *
 * @author AquaRuta VSA Engineering Team
 */
public class GeoDistanceHeuristic implements AStarAdmissibleHeuristic<Long> {

    private static final double MAX_SPEED_MPS = 22.22; // 80 km/h en metros por segundo
    private final Map<Long, RoadNode> nodeRegistry;

    public GeoDistanceHeuristic(Map<Long, RoadNode> nodeRegistry) {
        this.nodeRegistry = nodeRegistry;
    }

    @Override
    public double getCostEstimate(Long sourceNodeId, Long targetNodeId) {
        RoadNode source = nodeRegistry.get(sourceNodeId);
        RoadNode target = nodeRegistry.get(targetNodeId);

        if (source == null || target == null) {
            return 0.0;
        }

        double distanceMeters = source.toGeoPoint().distanceTo(target.toGeoPoint());
        // Tiempo mínimo teórico en segundos
        return distanceMeters / MAX_SPEED_MPS;
    }
}
