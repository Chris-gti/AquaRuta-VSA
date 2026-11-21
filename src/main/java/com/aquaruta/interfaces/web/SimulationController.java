package com.aquaruta.interfaces.web;

import com.aquaruta.application.dto.ReportResponseDto;
import com.aquaruta.application.dto.SimulationRainDto;
import com.aquaruta.application.dto.SimulationReportBurstDto;
import com.aquaruta.application.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el Modo Demo y Simulación Hidrológica.
 */
@RestController
@RequestMapping("/api/v1/simulation")
@Tag(name = "Modo Simulación (Demo)", description = "Inyección de lluvia artificial y ráfagas de reportes para demostración")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/rain")
    @Operation(summary = "Inyectar lluvia artificial en un cuadrante",
               description = "Modifica la precipitación en mm/h y actualiza dinámicamente las impedancias del grafo.")
    public ResponseEntity<Map<String, String>> injectRain(@Valid @RequestBody SimulationRainDto dto) {
        simulationService.injectRain(dto);
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", String.format("Lluvia de %.1f mm/h inyectada en %s", dto.precipitationMmH(), dto.quadrantId())
        ));
    }

    @PostMapping("/reports/burst")
    @Operation(summary = "Disparar ráfaga de reportes ciudadanos",
               description = "Genera múltiples reportes en un radio para forzar el quórum de consenso espacio-temporal.")
    public ResponseEntity<List<ReportResponseDto>> triggerReportBurst(@Valid @RequestBody SimulationReportBurstDto dto) {
        List<ReportResponseDto> reports = simulationService.triggerReportBurst(dto);
        return ResponseEntity.ok(reports);
    }

    @PostMapping("/reset")
    @Operation(summary = "Restablecer escenario a condiciones secas")
    public ResponseEntity<Map<String, String>> resetSimulation() {
        simulationService.resetWeather();
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Simulación restablecida a condiciones normales."
        ));
    }
}
