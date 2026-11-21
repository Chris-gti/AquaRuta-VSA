package com.aquaruta.application.dto;

import com.aquaruta.domain.model.ReportStatus;
import com.aquaruta.domain.model.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * DTO de respuesta para reportes ciudadanos.
 */
@Schema(description = "Datos detallados de un reporte ciudadano")
public record ReportResponseDto(
        String id,
        ReportType reportType,
        String description,
        double latitude,
        double longitude,
        Instant createdAt,
        ReportStatus status,
        String clusterId
) {}
