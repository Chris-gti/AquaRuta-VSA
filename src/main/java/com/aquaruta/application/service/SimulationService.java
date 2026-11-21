package com.aquaruta.application.service;

import com.aquaruta.application.dto.ReportCreateDto;
import com.aquaruta.application.dto.ReportResponseDto;
import com.aquaruta.application.dto.SimulationRainDto;
import com.aquaruta.application.dto.SimulationReportBurstDto;
import com.aquaruta.domain.model.WeatherCell;
import com.aquaruta.domain.repository.WeatherCellRepositoryPort;
import com.aquaruta.domain.strategy.RiskStrategyFactory;
import com.aquaruta.infrastructure.graph.RoadNetworkGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Servicio para orquestar escenarios interactivos de simulación (Modo Demo).
 */
@Service
public class SimulationService {

    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);
    private final Random random = new Random(42);

    private final WeatherCellRepositoryPort weatherCellRepository;
    private final WaterOutageService waterOutageService;
    private final RoadNetworkGraph roadNetworkGraph;
    private final RiskStrategyFactory riskStrategyFactory;

    public SimulationService(WeatherCellRepositoryPort weatherCellRepository,
                             WaterOutageService waterOutageService,
                             RoadNetworkGraph roadNetworkGraph) {
        this.weatherCellRepository = weatherCellRepository;
        this.waterOutageService = waterOutageService;
        this.roadNetworkGraph = roadNetworkGraph;
        this.riskStrategyFactory = new RiskStrategyFactory();
    }

    /**
     * Inyecta lluvia artificial en un cuadrante y actualiza los pesos de la red vial en vivo.
     */
    public void injectRain(SimulationRainDto dto) {
        log.info("Simulación: Inyectando {} mm/h en cuadrante {}", dto.precipitationMmH(), dto.quadrantId());

        weatherCellRepository.findById(dto.quadrantId()).ifPresent(cell -> {
            cell.setPrecipitationMmH(dto.precipitationMmH());
            weatherCellRepository.save(cell);
        });

        // Recalcular pesos en memoria
        roadNetworkGraph.updateAllEdgeWeights(
                riskStrategyFactory.getStrategy(dto.precipitationMmH() > 0.0),
                dto.precipitationMmH(),
                Collections.emptyList()
        );
    }

    /**
     * Dispara una ráfaga de reportes ciudadanos en una vecindad para demostrar el consenso.
     */
    public List<ReportResponseDto> triggerReportBurst(SimulationReportBurstDto dto) {
        log.info("Simulación: Disparando ráfaga de {} reportes tipo {} en ({}, {})",
                dto.count(), dto.reportType(), dto.centerLat(), dto.centerLon());

        List<ReportResponseDto> createdReports = new ArrayList<>();
        double radiusDegrees = dto.radiusMeters() / 111320.0;

        for (int i = 1; i <= dto.count(); i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double r = random.nextDouble() * radiusDegrees;
            double lat = dto.centerLat() + (r * Math.cos(angle));
            double lon = dto.centerLon() + (r * Math.sin(angle));

            String desc = String.format("Reporte simulado #%d de %s en la zona", i, dto.reportType());
            ReportCreateDto createDto = new ReportCreateDto(dto.reportType(), desc, lat, lon);
            createdReports.add(waterOutageService.createReport(createDto));
        }

        return createdReports;
    }

    /**
     * Restablece el clima a seco (0 mm/h) en todos los cuadrantes.
     */
    public void resetWeather() {
        List<WeatherCell> cells = weatherCellRepository.findAll();
        for (WeatherCell cell : cells) {
            cell.setPrecipitationMmH(0.0);
            weatherCellRepository.save(cell);
        }
        roadNetworkGraph.updateAllEdgeWeights(riskStrategyFactory.getNormalStrategy(), 0.0, Collections.emptyList());
        log.info("Simulación: Clima restablecido a condiciones secas en Villahermosa.");
    }
}
