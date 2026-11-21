package com.aquaruta.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO con el resultado del enrutamiento A* sobre la red vial.
 */
@Schema(description = "Respuesta detallada de ruta calculada con métricas hidrológicas")
public record RouteResponseDto(
        @Schema(description = "Puntos geográficos [lat, lon] de la ruta")
        List<List<Double>> pathCoordinates,

        @Schema(description = "Identificadores de nodos viales recorridos")
        List<Long> nodeIds,

        @Schema(description = "Distancia total recorrida en metros", example = "2450.5")
        double totalDistanceMeters,

        @Schema(description = "Tiempo estimado en minutos", example = "4.2")
        double estimatedTimeMinutes,

        @Schema(description = "Puntaje acumulado de penalización de riesgo hidráulico", example = "12.0")
        double totalRiskPenaltyScore,

        @Schema(description = "Cantidad de tramos anegados esquivados activamente", example = "2")
        int blockedStreetsAvoided,

        @Schema(description = "Altitud promedio del trayecto en m.s.n.m.", example = "10.4")
        double averageElevationMsnm,

        @Schema(description = "Estrategia de cálculo empleada", example = "FLOOD_RESILIENT_RISK_STRATEGY")
        String strategyUsed
) {}
