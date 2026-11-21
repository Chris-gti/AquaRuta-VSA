package com.aquaruta.application.service;

import com.aquaruta.application.dto.ReportCreateDto;
import com.aquaruta.application.dto.ReportResponseDto;
import com.aquaruta.application.dto.SimulationRainDto;
import com.aquaruta.application.dto.SimulationReportBurstDto;
import com.aquaruta.domain.model.ReportStatus;
import com.aquaruta.domain.model.ReportType;
import com.aquaruta.domain.model.WeatherCell;
import com.aquaruta.domain.repository.WeatherCellRepositoryPort;
import com.aquaruta.infrastructure.graph.RoadNetworkGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SimulationServiceTest {

    private List<WeatherCell> storedCells;
    private List<ReportCreateDto> submittedReports;
    private SimulationService simulationService;

    @BeforeEach
    void setUp() {
        storedCells = new ArrayList<>();
        submittedReports = new ArrayList<>();

        storedCells.add(new WeatherCell("QUAD_GAVIOTAS", "Gaviotas", 17.9850, -92.9100, 0.0, Instant.now()));

        WeatherCellRepositoryPort fakeWeatherRepo = new WeatherCellRepositoryPort() {
            @Override
            public WeatherCell save(WeatherCell cell) {
                storedCells.removeIf(c -> c.getId().equals(cell.getId()));
                storedCells.add(cell);
                return cell;
            }

            @Override
            public List<WeatherCell> saveAll(List<WeatherCell> cells) {
                return cells;
            }

            @Override
            public Optional<WeatherCell> findById(String id) {
                return storedCells.stream().filter(c -> c.getId().equals(id)).findFirst();
            }

            @Override
            public List<WeatherCell> findAll() {
                return storedCells;
            }

            @Override
            public Optional<WeatherCell> findCellForCoordinate(double lat, double lon) {
                return storedCells.stream().findFirst();
            }
        };

        WaterOutageService mockOutageService = new WaterOutageService(null, null, null) {
            @Override
            public ReportResponseDto createReport(ReportCreateDto dto) {
                submittedReports.add(dto);
                return new ReportResponseDto("rep-" + submittedReports.size(), dto.reportType(),
                        dto.description(), dto.latitude(), dto.longitude(), Instant.now(),
                        ReportStatus.PENDIENTE_VERIFICACION, null);
            }
        };

        RoadNetworkGraph roadNetwork = new RoadNetworkGraph();
        simulationService = new SimulationService(fakeWeatherRepo, mockOutageService, roadNetwork);
    }

    @Test
    @DisplayName("Debe inyectar lluvia en un cuadrante y actualizar el estado")
    void testInjectRain() {
        simulationService.injectRain(new SimulationRainDto("QUAD_GAVIOTAS", 65.0));

        WeatherCell updated = storedCells.stream()
                .filter(c -> c.getId().equals("QUAD_GAVIOTAS"))
                .findFirst().orElseThrow();

        assertEquals(65.0, updated.getPrecipitationMmH(), 0.001);
    }

    @Test
    @DisplayName("Debe generar ráfaga de reportes simulados en una vecindad")
    void testTriggerReportBurst() {
        SimulationReportBurstDto burstDto = new SimulationReportBurstDto(
                ReportType.CORTE_AGUA, 17.9890, -92.9250, 4, 100.0);

        List<ReportResponseDto> created = simulationService.triggerReportBurst(burstDto);

        assertEquals(4, created.size());
        assertEquals(4, submittedReports.size());
        assertTrue(submittedReports.stream().allMatch(r -> r.reportType() == ReportType.CORTE_AGUA));
    }
}
