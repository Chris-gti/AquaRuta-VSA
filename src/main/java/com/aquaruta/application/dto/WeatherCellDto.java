package com.aquaruta.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * DTO para información meteorológica por cuadrante.
 */
@Schema(description = "Estado meteorológico de un cuadrante urbano de Villahermosa")
public record WeatherCellDto(
        String id,
        String name,
        double centerLat,
        double centerLon,
        double precipitationMmH,
        Instant lastUpdated
) {}
