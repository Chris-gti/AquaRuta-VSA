package com.aquaruta.domain.model;

import java.util.Collections;
import java.util.List;

/**
 * Objeto de valor que representa una ruta calculada entre un origen y un destino.
 *
 * @param coordinates             Secuencia ordenada de puntos geográficos que conforman la ruta
 * @param nodeIds                 Identificadores OSM de los nodos recorridos
 * @param totalDistanceMeters     Distancia total en metros
 * @param estimatedTimeMinutes    Tiempo estimado en automóvil considerando velocidades reglamentarias
 * @param totalRiskPenaltyScore   Puntaje acumulado de riesgo hídrico evitado / atravesado
 * @param blockedStreetsAvoided   Número de calles anegadas esquivadas activamente por el algoritmo
 * @param averageElevationMsnm    Altitud promedio de los nodos del trayecto
 *
 * @author AquaRuta VSA Engineering Team
 */
public record RoutePath(
        List<GeoPoint> coordinates,
        List<Long> nodeIds,
        double totalDistanceMeters,
        double estimatedTimeMinutes,
        double totalRiskPenaltyScore,
        int blockedStreetsAvoided,
        double averageElevationMsnm
) {

    public RoutePath {
        coordinates = coordinates != null ? List.copyOf(coordinates) : Collections.emptyList();
        nodeIds = nodeIds != null ? List.copyOf(nodeIds) : Collections.emptyList();
    }

    public static RoutePath empty() {
        return new RoutePath(Collections.emptyList(), Collections.emptyList(), 0.0, 0.0, 0.0, 0, 0.0);
    }

    public boolean isEmpty() {
        return coordinates.isEmpty();
    }
}
