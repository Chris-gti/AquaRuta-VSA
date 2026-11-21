package com.aquaruta.interfaces.web;

import com.aquaruta.application.dto.WeatherCellDto;
import com.aquaruta.application.service.WeatherGridService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para consulta de la cuadrícula meteorológica urbana.
 */
@RestController
@RequestMapping("/api/v1/weather")
@Tag(name = "Clima Urbano", description = "Monitoreo de precipitación en tiempo real por cuadrantes de Villahermosa")
public class WeatherController {

    private final WeatherGridService weatherGridService;

    public WeatherController(WeatherGridService weatherGridService) {
        this.weatherGridService = weatherGridService;
    }

    @GetMapping("/cells")
    @Operation(summary = "Obtener estado meteorológico de los cuadrantes urbanos")
    public ResponseEntity<List<WeatherCellDto>> getWeatherCells() {
        return ResponseEntity.ok(weatherGridService.getAllCells());
    }
}
