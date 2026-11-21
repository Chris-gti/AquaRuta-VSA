package com.aquaruta.application.service;

import com.aquaruta.application.dto.WeatherCellDto;
import com.aquaruta.domain.model.WeatherCell;
import com.aquaruta.domain.repository.WeatherCellRepositoryPort;
import com.aquaruta.domain.strategy.RiskStrategyFactory;
import com.aquaruta.infrastructure.external.weather.OpenMeteoClient;
import com.aquaruta.infrastructure.graph.RoadNetworkGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Servicio de monitoreo meteorológico por cuadrantes sobre el polígono céntrico.
 */
@Service
public class WeatherGridService {

    private static final Logger log = LoggerFactory.getLogger(WeatherGridService.class);

    private final WeatherCellRepositoryPort weatherCellRepository;
    private final OpenMeteoClient openMeteoClient;
    private final RoadNetworkGraph roadNetworkGraph;
    private final RiskStrategyFactory riskStrategyFactory;

    public WeatherGridService(WeatherCellRepositoryPort weatherCellRepository,
                              OpenMeteoClient openMeteoClient,
                              RoadNetworkGraph roadNetworkGraph) {
        this.weatherCellRepository = weatherCellRepository;
        this.openMeteoClient = openMeteoClient;
        this.roadNetworkGraph = roadNetworkGraph;
        this.riskStrategyFactory = new RiskStrategyFactory();
    }

    /**
     * Consulta automática periódica cada 30 minutos a Open-Meteo.
     */
    @Scheduled(fixedRate = 1800000, initialDelay = 10000)
    public void syncWeatherGrid() {
        log.info("Sincronizando cuadrícula meteorológica desde Open-Meteo...");
        List<WeatherCell> cells = weatherCellRepository.findAll();
        double maxPrecipitation = 0.0;

        for (WeatherCell cell : cells) {
            double currentRain = openMeteoClient.fetchCurrentPrecipitationMmH(cell.getCenterLat(), cell.getCenterLon());
            cell.setPrecipitationMmH(currentRain);
            weatherCellRepository.save(cell);
            if (currentRain > maxPrecipitation) {
                maxPrecipitation = currentRain;
            }
        }

        // Recalcular pesos del grafo vial
        roadNetworkGraph.updateAllEdgeWeights(
                riskStrategyFactory.getStrategy(maxPrecipitation > 0.0),
                maxPrecipitation,
                Collections.emptyList()
        );
    }

    public List<WeatherCellDto> getAllCells() {
        return weatherCellRepository.findAll().stream()
                .map(c -> new WeatherCellDto(c.getId(), c.getName(), c.getCenterLat(), c.getCenterLon(),
                        c.getPrecipitationMmH(), c.getLastUpdated()))
                .toList();
    }

    public void updateCellPrecipitation(String cellId, double precipitationMmH) {
        weatherCellRepository.findById(cellId).ifPresent(cell -> {
            cell.setPrecipitationMmH(precipitationMmH);
            weatherCellRepository.save(cell);
            log.info("Precipitación de {} actualizada a {} mm/h", cell.getName(), precipitationMmH);
        });
    }
}
