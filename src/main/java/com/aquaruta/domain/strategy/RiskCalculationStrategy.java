package com.aquaruta.domain.strategy;

import com.aquaruta.domain.model.RoadEdge;
import com.aquaruta.domain.model.RoadNode;

/**
 * Patrón Strategy para el cálculo de peso/costo en los arcos de la red vial.
 * <p>
 * Permite intercambiar dinámicamente los criterios de cálculo de impedancia
 * (modo clima normal vs. modo tormenta/resiliente) sin alterar el algoritmo
 * de enrutamiento A*.
 * </p>
 *
 * @author AquaRuta VSA Engineering Team
 */
public interface RiskCalculationStrategy {

    /**
     * Calcula la impedancia (peso) ponderada de un tramo vial.
     *
     * @param edge                      Arista de la red vial
     * @param targetNode                Nodo de llegada (con altimetría m.s.n.m.)
     * @param localPrecipitationMmH     Precipitación actual en el cuadrante (mm/h)
     * @param activeFloodReportsNearby  Cantidad de reportes activos de anegamiento en el tramo
     * @return Peso de costo ponderado para el algoritmo A*
     */
    double calculateWeight(RoadEdge edge,
                           RoadNode targetNode,
                           double localPrecipitationMmH,
                           int activeFloodReportsNearby);

    /**
     * Identificador legible de la estrategia.
     *
     * @return Nombre de la estrategia
     */
    String getStrategyName();
}
