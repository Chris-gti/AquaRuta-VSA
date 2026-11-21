package com.aquaruta.interfaces.web;

import com.aquaruta.application.service.MapIngestionService;
import com.aquaruta.infrastructure.graph.RoadNetworkGraph;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST para administración del proceso ETL y recarga del grafo.
 */
@RestController
@RequestMapping("/api/v1/ingestion")
@Tag(name = "Administración ETL", description = "Control de ingesta de OpenStreetMap y carga de grafo a RAM")
public class IngestionController {

    private final MapIngestionService ingestionService;
    private final RoadNetworkGraph roadNetworkGraph;

    public IngestionController(MapIngestionService ingestionService, RoadNetworkGraph roadNetworkGraph) {
        this.ingestionService = ingestionService;
        this.roadNetworkGraph = roadNetworkGraph;
    }

    @PostMapping("/reload")
    @Operation(summary = "Recargar red vial desde la base de datos a memoria RAM")
    public ResponseEntity<Map<String, Object>> reloadGraph() {
        ingestionService.loadGraphFromDatabase();
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "nodesInRam", roadNetworkGraph.getVertexCount(),
                "edgesInRam", roadNetworkGraph.getEdgeCount()
        ));
    }

    @PostMapping("/reingest")
    @Operation(summary = "Reejecutar ingesta completa desde OpenStreetMap (Overpass API)")
    public ResponseEntity<Map<String, Object>> reingestFromOsm() {
        ingestionService.ingestAndPersist();
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "nodesInRam", roadNetworkGraph.getVertexCount(),
                "edgesInRam", roadNetworkGraph.getEdgeCount()
        ));
    }
}
