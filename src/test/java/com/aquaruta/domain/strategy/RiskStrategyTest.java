package com.aquaruta.domain.strategy;

import com.aquaruta.domain.model.RoadEdge;
import com.aquaruta.domain.model.RoadNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RiskStrategyTest {

    private NormalWeatherRiskStrategy normalStrategy;
    private FloodResilientRiskStrategy floodStrategy;

    @BeforeEach
    void setUp() {
        normalStrategy = new NormalWeatherRiskStrategy();
        floodStrategy = new FloodResilientRiskStrategy();
    }

    @Test
    @DisplayName("En clima seco, la estrategia normal debe calcular el tiempo de viaje ideal")
    void testNormalStrategyWeight() {
        RoadNode node = new RoadNode(101L, 17.9900, -92.9300, 10.0);
        RoadEdge edge = new RoadEdge("E1", 100L, 101L, "Paseo Tabasco", "primary", 500.0, false, 50, 1.0);

        double weight = normalStrategy.calculateWeight(edge, node, 0.0, 0);

        // Longitud 500m / (50 km/h = 13.88 m/s) * 0.9 (primaria) ~ 32.4 segundos
        assertTrue(weight > 25.0 && weight < 45.0, "El peso base debe ser razonable: " + weight);
    }

    @Test
    @DisplayName("Con lluvia torrencial y cota baja en Villahermosa (Gaviotas), el peso debe incrementarse significativamente")
    void testFloodStrategyWithHeavyRainAndLowElevation() {
        // Cota baja: 4.0 msnm (similar al sector Gaviotas o zona baja del malecón)
        RoadNode lowNode = new RoadNode(201L, 17.9850, -92.9100, 4.0);
        RoadEdge edge = new RoadEdge("E2", 200L, 201L, "Paseo de la Choca", "secondary", 400.0, false, 40, 1.0);

        double dryWeight = floodStrategy.calculateWeight(edge, lowNode, 0.0, 0);
        double heavyRainWeight = floodStrategy.calculateWeight(edge, lowNode, 60.0, 0); // 60 mm/h lluvia torrencial

        assertTrue(heavyRainWeight > (dryWeight * 5.0),
                "La lluvia torrencial en cota baja debe multiplicar el peso al menos 5 veces. Seco=" + dryWeight + ", Lluvia=" + heavyRainWeight);
    }

    @Test
    @DisplayName("Si hay 3 o más reportes de anegamiento, el tramo debe considerarse bloqueado/impenetrable")
    void testBlockedEdgeWithActiveReports() {
        RoadNode node = new RoadNode(301L, 17.9800, -92.9200, 6.0);
        RoadEdge edge = new RoadEdge("E3", 300L, 301L, "Av. Francisco Javier Mina", "tertiary", 300.0, false, 40, 1.0);

        double weight = floodStrategy.calculateWeight(edge, node, 10.0, 3);
        assertEquals(1_000_000.0, weight, "Con 3 reportes debe alcanzar la penalización máxima de bloqueo");
    }
}
