package com.aquaruta.application.dto;

import com.aquaruta.domain.model.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * DTO para alertas validadas por consenso espacio-temporal.
 */
@Schema(description = "Alerta ciudadana confirmada por quórum")
public record ValidatedAlertResponseDto(
        String id,
        ReportType alertType,
        double centroidLat,
        double centroidLon,
        double radiusMeters,
        int reportCount,
        Instant confirmedAt,
        String status
) {}
