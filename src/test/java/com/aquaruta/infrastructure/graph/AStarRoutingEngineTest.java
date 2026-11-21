package com.aquaruta.infrastructure.graph;

import com.aquaruta.domain.model.RoadEdge;
import com.aquaruta.domain.model.RoadNode;
import com.aquaruta.domain.model.RoutePath;
import com.aquaruta.domain.strategy.FloodResilientRiskStrategy;
import com.aquaruta.domain.strategy.NormalWeatherRiskStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AStarRoutingEngineTest {

    private RoadNetworkGraph roadNetwork;
    private AStarRoutingEngine routingEngine;

    @BeforeEach
    void setUp() {
        roadNetwork = new RoadNetworkGraph();
        routingEngine = new AStarRoutingEngine(roadNetwork);

        // Nodos: Origen (1) -> Destino (4)
        // Camino A (Bajo, directo): 1 -> 2 -> 4 (Cota 4.0m - inundable)
        // Camino B (Elevado, rodeo seguro): 1 -> 3 -> 4 (Cota 12.0m - seguro)
        RoadNode n1 = new RoadNode(1L, 17.9900, -92.9300, 10.0);
        RoadNode n2 = new RoadNode(2L, 17.9890, -92.9250, 4.0);  // Cota baja en Villahermosa
        RoadNode n3 = new RoadNode(3L, 17.9950, -92.9250, 12.0); // Cota alta
        RoadNode n4 = new RoadNode(4L, 17.9900, -92.9200, 9.0);

        roadNetwork.addNode(n1);
        roadNetwork.addNode(n2);
        roadNetwork.addNode(n3);
        roadNetwork.addNode(n4);

        // Aristas Camino A (Corto y directo: 350m + 350m = 700m)
        roadNetwork.addEdge(new RoadEdge("E1_2", 1L, 2L, "Calle Baja 1", "residential", 350.0, true, 40, 1.0));
        roadNetwork.addEdge(new RoadEdge("E2_4", 2L, 4L, "Calle Baja 2", "residential", 350.0, true, 40, 1.0));

        // Aristas Camino B (Largo: 900m + 900m = 1800m)
        roadNetwork.addEdge(new RoadEdge("E1_3", 1L, 3L, "Av. Alta 1", "primary", 900.0, true, 50, 1.0));
        roadNetwork.addEdge(new RoadEdge("E3_4", 3L, 4L, "Av. Alta 2", "primary", 900.0, true, 50, 1.0));
    }

    @Test
    @DisplayName("En clima seco, A* debe elegir el camino directo más corto (1 -> 2 -> 4)")
    void testRoutingInDryWeather() {
        roadNetwork.updateAllEdgeWeights(new NormalWeatherRiskStrategy(), 0.0, Collections.emptyList());

        Optional<RoutePath> pathOpt = routingEngine.findShortestPath(17.9900, -92.9300, 17.9900, -92.9200);

        assertTrue(pathOpt.isPresent(), "Debe encontrar una ruta");
        RoutePath path = pathOpt.get();

        assertEquals(List.of(1L, 2L, 4L), path.nodeIds(),
                "En seco debe tomar la ruta corta a través del nodo 2");
        assertEquals(700.0, path.totalDistanceMeters(), 1.0);
    }

    @Test
    @DisplayName("Bajo tormenta severa (80 mm/h), A* debe desviar la ruta hacia la cota alta segura (1 -> 3 -> 4)")
    void testDynamicReroutingDuringStorm() {
        // Inyectamos tormenta de 80 mm/h con la estrategia resiliente a inundaciones
        roadNetwork.updateAllEdgeWeights(new FloodResilientRiskStrategy(), 80.0, Collections.emptyList());

        Optional<RoutePath> pathOpt = routingEngine.findShortestPath(17.9900, -92.9300, 17.9900, -92.9200);

        assertTrue(pathOpt.isPresent(), "Debe encontrar una ruta alternativa");
        RoutePath path = pathOpt.get();

        assertEquals(List.of(1L, 3L, 4L), path.nodeIds(),
                "Bajo lluvia intensa, A* debe esquivar el nodo 2 (cota baja) y tomar el rodeo seguro por el nodo 3");
        assertEquals(1800.0, path.totalDistanceMeters(), 1.0);
        assertTrue(path.averageElevationMsnm() > 10.0, "La elevación promedio de la ruta segura debe ser superior a 10 msnm");
    }
}
