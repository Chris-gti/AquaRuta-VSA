package com.aquaruta.interfaces.web;

import com.aquaruta.application.dto.RouteRequestDto;
import com.aquaruta.application.dto.RouteResponseDto;
import com.aquaruta.application.service.RoutingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el cálculo de rutas óptimas anti-inundaciones.
 */
@RestController
@RequestMapping("/api/v1/routes")
@Tag(name = "Enrutamiento Vial", description = "Cálculo de rutas óptimas y resilientes sobre el grafo de Villahermosa")
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping
    @Operation(summary = "Calcular ruta óptima entre dos coordenadas",
               description = "Ejecuta A* con heurística geodésica y ponderación dinámica por altitud y lluvia.")
    public ResponseEntity<RouteResponseDto> getRoute(
            @Parameter(description = "Latitud de inicio", example = "17.9985") @RequestParam double startLat,
            @Parameter(description = "Longitud de inicio", example = "-92.9372") @RequestParam double startLon,
            @Parameter(description = "Latitud de destino", example = "17.9835") @RequestParam double endLat,
            @Parameter(description = "Longitud de destino", example = "-92.9090") @RequestParam double endLon,
            @Parameter(description = "Modo de evasión de inundaciones", example = "true")
            @RequestParam(required = false, defaultValue = "true") boolean resilientMode) {

        RouteRequestDto request = new RouteRequestDto(startLat, startLon, endLat, endLon, resilientMode);
        return routingService.calculateRoute(request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
