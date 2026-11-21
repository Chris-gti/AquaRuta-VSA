package com.aquaruta.application.service;

import com.aquaruta.domain.model.RoadEdge;
import com.aquaruta.domain.model.RoadNode;
import com.aquaruta.domain.repository.RoadEdgeRepositoryPort;
import com.aquaruta.domain.repository.RoadNodeRepositoryPort;
import com.aquaruta.infrastructure.external.overpass.OverpassApiClient;
import com.aquaruta.infrastructure.graph.RoadNetworkGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de ingesta ETL y carga de la red vial en memoria RAM.
 */
@Service
public class MapIngestionService {

    private static final Logger log = LoggerFactory.getLogger(MapIngestionService.class);

    private final OverpassApiClient overpassClient;
    private final RoadNodeRepositoryPort nodeRepository;
    private final RoadEdgeRepositoryPort edgeRepository;
    private final RoadNetworkGraph roadNetworkGraph;

    @Value("${aquaruta.geo.bbox.min-lat:17.9650}")
    private double minLat;

    @Value("${aquaruta.geo.bbox.min-lon:-92.9650}")
    private double minLon;

    @Value("${aquaruta.geo.bbox.max-lat:18.0150}")
    private double maxLat;

    @Value("${aquaruta.geo.bbox.max-lon:-92.9050}")
    private double maxLon;

    public MapIngestionService(OverpassApiClient overpassClient,
                               RoadNodeRepositoryPort nodeRepository,
                               RoadEdgeRepositoryPort edgeRepository,
                               RoadNetworkGraph roadNetworkGraph) {
        this.overpassClient = overpassClient;
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.roadNetworkGraph = roadNetworkGraph;
    }

    /**
     * Inicializa el grafo vial al arrancar la aplicación.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeOnStartup() {
        log.info("Inicializando red vial de Villahermosa en AquaRuta VSA...");
        long startTime = System.currentTimeMillis();

        long nodeCount = nodeRepository.count();
        long edgeCount = edgeRepository.count();

        if (nodeCount == 0 || edgeCount == 0) {
            log.info("Base de datos sin cartografía. Ejecutando ETL desde Overpass API / dataset local...");
            ingestAndPersist();
        } else {
            log.info("Cargando red vial desde PostGIS a memoria RAM ({} nodos, {} tramos)...", nodeCount, edgeCount);
            loadGraphFromDatabase();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("Grafo vial listo en memoria en {} ms. (Nodos: {}, Aristas: {})",
                elapsed, roadNetworkGraph.getVertexCount(), roadNetworkGraph.getEdgeCount());
    }

    /**
     * Descarga datos de OpenStreetMap, interpola altitudes, persiste en PostGIS y carga a memoria.
     */
    @Transactional
    public synchronized void ingestAndPersist() {
        OverpassApiClient.IngestedGraphData data = overpassClient.fetchRoadNetwork(minLat, minLon, maxLat, maxLon);

        nodeRepository.saveAll(data.nodes());
        edgeRepository.saveAll(data.edges());

        loadIntoMemory(data.nodes(), data.edges());
    }

    /**
     * Carga el grafo a memoria RAM desde PostgreSQL en menos de 2 segundos.
     */
    public synchronized void loadGraphFromDatabase() {
        List<RoadNode> nodes = nodeRepository.findAll();
        List<RoadEdge> edges = edgeRepository.findAll();
        loadIntoMemory(nodes, edges);
    }

    private void loadIntoMemory(List<RoadNode> nodes, List<RoadEdge> edges) {
        roadNetworkGraph.clear();
        for (RoadNode n : nodes) {
            roadNetworkGraph.addNode(n);
        }
        for (RoadEdge e : edges) {
            roadNetworkGraph.addEdge(e);
        }
    }
}
