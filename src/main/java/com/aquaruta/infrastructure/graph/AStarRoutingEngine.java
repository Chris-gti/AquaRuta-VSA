package com.aquaruta.infrastructure.graph;

import com.aquaruta.domain.model.GeoPoint;
import com.aquaruta.domain.model.RoadEdge;
import com.aquaruta.domain.model.RoadNode;
import com.aquaruta.domain.model.RoutePath;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Motor de enrutamiento resiliente a inundaciones basado en el algoritmo A* de JGraphT.
 * <p>
 * Emplea la heurística de distancia admisible {@link GeoDistanceHeuristic} y calcula
 * la ruta óptima entre dos coordenadas geográficas sobre la red vial de Villahermosa.
 * </p>
 *
 * @author AquaRuta VSA Engineering Team
 */
@Service
public class AStarRoutingEngine {

    private static final Logger log = LoggerFactory.getLogger(AStarRoutingEngine.class);

    private final RoadNetworkGraph roadNetwork;

    public AStarRoutingEngine(RoadNetworkGraph roadNetwork) {
        this.roadNetwork = roadNetwork;
    }

    /**
     * Calcula la ruta óptima entre coordenadas de origen y destino.
     *
     * @param startLat Latitud de inicio
     * @param startLon Longitud de inicio
     * @param endLat   Latitud de destino
     * @param endLon   Longitud de destino
     * @return {@link RoutePath} con la geometría y métricas del trayecto
     */
    public Optional<RoutePath> findShortestPath(double startLat, double startLon, double endLat, double endLon) {
        roadNetwork.readLock().lock();
        try {
            Optional<RoadNode> startNodeOpt = roadNetwork.findNearestNode(startLat, startLon);
            Optional<RoadNode> endNodeOpt = roadNetwork.findNearestNode(endLat, endLon);

            if (startNodeOpt.isEmpty() || endNodeOpt.isEmpty()) {
                log.warn("No se encontraron nodos viales cercanos para las coordenadas proporcionadas.");
                return Optional.empty();
            }

            RoadNode startNode = startNodeOpt.get();
            RoadNode endNode = endNodeOpt.get();

            if (startNode.getId().equals(endNode.getId())) {
                List<GeoPoint> singlePt = List.of(startNode.toGeoPoint());
                return Optional.of(new RoutePath(
                        singlePt,
                        List.of(startNode.getId()),
                        0.0,
                        0.0,
                        0.0,
                        0,
                        startNode.getElevationMsnm()
                ));
            }

            GeoDistanceHeuristic heuristic = new GeoDistanceHeuristic(roadNetwork.getNodeRegistry());
            AStarShortestPath<Long, DefaultWeightedEdge> aStar =
                    new AStarShortestPath<>(roadNetwork.getRawGraph(), heuristic);

            GraphPath<Long, DefaultWeightedEdge> path = aStar.getPath(startNode.getId(), endNode.getId());
            if (path == null) {
                log.warn("No existe ruta transitable entre el nodo {} y {}", startNode.getId(), endNode.getId());
                return Optional.empty();
            }

            List<Long> vertexList = path.getVertexList();
            List<GeoPoint> coordinates = new ArrayList<>();
            double totalElevation = 0.0;
            int nodeCount = 0;

            for (Long vertexId : vertexList) {
                RoadNode node = roadNetwork.getNode(vertexId);
                if (node != null) {
                    coordinates.add(node.toGeoPoint());
                    totalElevation += node.getElevationMsnm();
                    nodeCount++;
                }
            }

            double totalDistance = 0.0;
            double estimatedTimeSeconds = 0.0;
            double totalRiskPenalty = 0.0;
            int avoidedRiskStreets = 0;

            for (DefaultWeightedEdge edge : path.getEdgeList()) {
                RoadEdge roadEdge = roadNetwork.getRoadEdge(edge);
                if (roadEdge != null) {
                    totalDistance += roadEdge.getLengthMeters();
                    double speedMps = (roadEdge.getMaxspeed() * 1000.0) / 3600.0;
                    estimatedTimeSeconds += roadEdge.getLengthMeters() / Math.max(5.0, speedMps);

                    double edgeWeight = roadNetwork.getRawGraph().getEdgeWeight(edge);
                    totalRiskPenalty += (edgeWeight - roadEdge.getLengthMeters());

                    if (roadEdge.getCurrentDynamicWeight() > (roadEdge.getLengthMeters() * 2.5)) {
                        avoidedRiskStreets++;
                    }
                }
            }

            double avgElevation = nodeCount > 0 ? (totalElevation / nodeCount) : 8.0;
            double estimatedMinutes = Math.max(1.0, Math.round((estimatedTimeSeconds / 60.0) * 10.0) / 10.0);

            RoutePath routePath = new RoutePath(
                    coordinates,
                    vertexList,
                    Math.round(totalDistance * 10.0) / 10.0,
                    estimatedMinutes,
                    Math.round(totalRiskPenalty * 10.0) / 10.0,
                    avoidedRiskStreets,
                    Math.round(avgElevation * 10.0) / 10.0
            );

            log.info("Ruta A* calculada: {} nodos, {} m, {} min, elevación prom: {} msnm",
                    vertexList.size(), routePath.totalDistanceMeters(), routePath.estimatedTimeMinutes(), routePath.averageElevationMsnm());

            return Optional.of(routePath);
        } finally {
            roadNetwork.readLock().unlock();
        }
    }
}
