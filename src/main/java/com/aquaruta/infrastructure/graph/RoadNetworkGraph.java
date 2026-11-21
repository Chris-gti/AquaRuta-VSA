package com.aquaruta.infrastructure.graph;

import com.aquaruta.domain.model.CitizenReport;
import com.aquaruta.domain.model.GeoPoint;
import com.aquaruta.domain.model.RoadEdge;
import com.aquaruta.domain.model.RoadNode;
import com.aquaruta.domain.strategy.RiskCalculationStrategy;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Gestor del grafo vial en memoria RAM para Villahermosa utilizando JGraphT.
 * <p>
 * Mantiene la topología vial dirigida y ponderada en memoria para permitir
 * cálculos de rutas en submilisegundos mediante A*. Ofrece soporte para actualización
 * atómica de pesos dinámicos ante eventos de lluvia y anegamientos.
 * </p>
 *
 * @author AquaRuta VSA Engineering Team
 */
@Component
public class RoadNetworkGraph {

    private static final Logger log = LoggerFactory.getLogger(RoadNetworkGraph.class);

    private final SimpleDirectedWeightedGraph<Long, DefaultWeightedEdge> graph;
    private final Map<Long, RoadNode> nodeRegistry;
    private final Map<DefaultWeightedEdge, RoadEdge> edgeRegistry;
    private final Map<String, DefaultWeightedEdge> edgeIdToJGraphTEdge;
    private final ReentrantReadWriteLock lock;

    public RoadNetworkGraph() {
        this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        this.nodeRegistry = new ConcurrentHashMap<>();
        this.edgeRegistry = new ConcurrentHashMap<>();
        this.edgeIdToJGraphTEdge = new ConcurrentHashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * Limpia la estructura completa del grafo.
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            edgeRegistry.clear();
            edgeIdToJGraphTEdge.clear();
            nodeRegistry.clear();
            List<DefaultWeightedEdge> edges = new ArrayList<>(graph.edgeSet());
            for (DefaultWeightedEdge e : edges) {
                graph.removeEdge(e);
            }
            List<Long> vertices = new ArrayList<>(graph.vertexSet());
            for (Long v : vertices) {
                graph.removeVertex(v);
            }
            log.info("Grafo vial reiniciado en memoria.");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Agrega un nodo vial al grafo.
     */
    public void addNode(RoadNode node) {
        if (node == null) return;
        lock.writeLock().lock();
        try {
            nodeRegistry.put(node.getId(), node);
            graph.addVertex(node.getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Agrega una arista al grafo con su peso inicial.
     */
    public void addEdge(RoadEdge edge) {
        if (edge == null) return;
        lock.writeLock().lock();
        try {
            if (!graph.containsVertex(edge.getSourceNodeId()) || !graph.containsVertex(edge.getTargetNodeId())) {
                return;
            }

            DefaultWeightedEdge jgEdge = graph.addEdge(edge.getSourceNodeId(), edge.getTargetNodeId());
            if (jgEdge != null) {
                graph.setEdgeWeight(jgEdge, edge.getCurrentDynamicWeight());
                edgeRegistry.put(jgEdge, edge);
                edgeIdToJGraphTEdge.put(edge.getId(), jgEdge);
            }

            // Si la calle no es de sentido único (oneway=false), agregar arista en sentido inverso
            if (!edge.isOneway()) {
                String reverseId = edge.getId() + "_rev";
                RoadEdge reverseEdge = new RoadEdge(
                        reverseId,
                        edge.getTargetNodeId(),
                        edge.getSourceNodeId(),
                        edge.getStreetName(),
                        edge.getHighwayType(),
                        edge.getLengthMeters(),
                        false,
                        edge.getMaxspeed(),
                        edge.getBaseRiskFactor()
                );
                DefaultWeightedEdge revJgEdge = graph.addEdge(edge.getTargetNodeId(), edge.getSourceNodeId());
                if (revJgEdge != null) {
                    graph.setEdgeWeight(revJgEdge, reverseEdge.getCurrentDynamicWeight());
                    edgeRegistry.put(revJgEdge, reverseEdge);
                    edgeIdToJGraphTEdge.put(reverseId, revJgEdge);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Recalcula y actualiza atómicamente todos los pesos de las aristas del grafo
     * utilizando una estrategia de riesgo dada, niveles de precipitación y reportes activos.
     */
    public void updateAllEdgeWeights(RiskCalculationStrategy strategy,
                                     double currentPrecipitationMmH,
                                     List<CitizenReport> activeFloodReports) {
        lock.writeLock().lock();
        try {
            for (Map.Entry<DefaultWeightedEdge, RoadEdge> entry : edgeRegistry.entrySet()) {
                DefaultWeightedEdge jgEdge = entry.getKey();
                RoadEdge roadEdge = entry.getValue();
                RoadNode targetNode = nodeRegistry.get(roadEdge.getTargetNodeId());

                // Contar reportes de anegamiento en un radio de 120m del nodo de destino
                int reportsNearby = 0;
                if (targetNode != null && activeFloodReports != null) {
                    GeoPoint targetPoint = targetNode.toGeoPoint();
                    for (CitizenReport report : activeFloodReports) {
                        if (targetPoint.distanceTo(report.toGeoPoint()) <= 120.0) {
                            reportsNearby++;
                        }
                    }
                }

                double weight = strategy.calculateWeight(roadEdge, targetNode, currentPrecipitationMmH, reportsNearby);
                roadEdge.setCurrentDynamicWeight(weight);
                graph.setEdgeWeight(jgEdge, weight);
            }
            log.info("Pesos de {} aristas recalculados exitosamente con estrategia: {}",
                    edgeRegistry.size(), strategy.getStrategyName());
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Encuentra el nodo vial en memoria más cercano a una coordenada dada (Spatial Snapping).
     */
    public Optional<RoadNode> findNearestNode(double latitude, double longitude) {
        lock.readLock().lock();
        try {
            if (nodeRegistry.isEmpty()) {
                return Optional.empty();
            }
            GeoPoint targetPoint = new GeoPoint(latitude, longitude);
            RoadNode bestNode = null;
            double minDistance = Double.MAX_VALUE;

            for (RoadNode node : nodeRegistry.values()) {
                double dist = targetPoint.distanceTo(node.toGeoPoint());
                if (dist < minDistance) {
                    minDistance = dist;
                    bestNode = node;
                }
            }
            return Optional.ofNullable(bestNode);
        } finally {
            lock.readLock().unlock();
        }
    }

    public SimpleDirectedWeightedGraph<Long, DefaultWeightedEdge> getRawGraph() {
        return graph;
    }

    public Map<Long, RoadNode> getNodeRegistry() {
        return Collections.unmodifiableMap(nodeRegistry);
    }

    public Map<DefaultWeightedEdge, RoadEdge> getEdgeRegistry() {
        return Collections.unmodifiableMap(edgeRegistry);
    }

    public RoadNode getNode(Long id) {
        return nodeRegistry.get(id);
    }

    public RoadEdge getRoadEdge(DefaultWeightedEdge edge) {
        return edgeRegistry.get(edge);
    }

    public int getVertexCount() {
        return graph.vertexSet().size();
    }

    public int getEdgeCount() {
        return graph.edgeSet().size();
    }

    public ReentrantReadWriteLock.ReadLock readLock() {
        return lock.readLock();
    }
}
