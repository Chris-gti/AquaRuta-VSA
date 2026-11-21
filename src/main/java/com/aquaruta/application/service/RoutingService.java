package com.aquaruta.application.service;

import com.aquaruta.application.dto.RouteRequestDto;
import com.aquaruta.application.dto.RouteResponseDto;
import com.aquaruta.domain.model.CitizenReport;
import com.aquaruta.domain.model.GeoPoint;
import com.aquaruta.domain.model.ReportType;
import com.aquaruta.domain.model.RoutePath;
import com.aquaruta.domain.repository.CitizenReportRepositoryPort;
import com.aquaruta.domain.repository.WeatherCellRepositoryPort;
import com.aquaruta.domain.strategy.RiskCalculationStrategy;
import com.aquaruta.domain.strategy.RiskStrategyFactory;
import com.aquaruta.infrastructure.graph.AStarRoutingEngine;
import com.aquaruta.infrastructure.graph.RoadNetworkGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para cálculo de rutas viales resilientes a inundaciones.
 */
@Service
public class RoutingService {

    private static final Logger log = LoggerFactory.getLogger(RoutingService.class);

    private final AStarRoutingEngine routingEngine;
    private final RoadNetworkGraph roadNetworkGraph;
    private final WeatherCellRepositoryPort weatherCellRepository;
    private final CitizenReportRepositoryPort reportRepository;
    private final RiskStrategyFactory riskStrategyFactory;

    public RoutingService(AStarRoutingEngine routingEngine,
                          RoadNetworkGraph roadNetworkGraph,
                          WeatherCellRepositoryPort weatherCellRepository,
                          CitizenReportRepositoryPort reportRepository) {
        this.routingEngine = routingEngine;
        this.roadNetworkGraph = roadNetworkGraph;
        this.weatherCellRepository = weatherCellRepository;
        this.reportRepository = reportRepository;
        this.riskStrategyFactory = new RiskStrategyFactory();
    }

    /**
     * Calcula la ruta óptima entre dos coordenadas.
     */
    public Optional<RouteResponseDto> calculateRoute(RouteRequestDto request) {
        log.info("Calculando ruta: ({}, {}) -> ({}, {}), Modo Resiliente={}",
                request.startLat(), request.startLon(), request.endLat(), request.endLon(), request.isResilientMode());

        // Obtener precipitación del cuadrante de origen
        double rainMmH = weatherCellRepository.findCellForCoordinate(request.startLat(), request.startLon())
                .map(c -> c.getPrecipitationMmH())
                .orElse(0.0);

        // Reportes activos de anegamiento en las últimas 3 horas
        List<CitizenReport> activeFloodReports = reportRepository.findRecentActiveReports(
                ReportType.CALLE_INUNDADA, Instant.now().minus(Duration.ofHours(3)));

        // Seleccionar estrategia de riesgo
        RiskCalculationStrategy strategy = riskStrategyFactory.getStrategy(request.isResilientMode());

        // Actualizar pesos del grafo vial
        roadNetworkGraph.updateAllEdgeWeights(strategy, rainMmH, activeFloodReports);

        // Ejecutar A*
        Optional<RoutePath> pathOpt = routingEngine.findShortestPath(
                request.startLat(), request.startLon(), request.endLat(), request.endLon());

        return pathOpt.map(path -> {
            List<List<Double>> coords = path.coordinates().stream()
                    .map(p -> List.of(p.latitude(), p.longitude()))
                    .toList();

            return new RouteResponseDto(
                    coords,
                    path.nodeIds(),
                    path.totalDistanceMeters(),
                    path.estimatedTimeMinutes(),
                    path.totalRiskPenaltyScore(),
                    path.blockedStreetsAvoided(),
                    path.averageElevationMsnm(),
                    strategy.getStrategyName()
            );
        });
    }
}
