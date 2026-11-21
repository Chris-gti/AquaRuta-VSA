package com.aquaruta.application.dto;

import com.aquaruta.domain.model.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para simulación de ráfagas de reportes ciudadanos simultáneos.
 */
@Schema(description = "Disparo de múltiples reportes en un punto para demostrar el consenso espacial")
public record SimulationReportBurstDto(
        @Schema(description = "Tipo de incidencia a simular", example = "CORTE_AGUA")
        @NotNull ReportType reportType,

        @Schema(description = "Latitud del epicentro de los reportes", example = "17.9890")
        @NotNull Double centerLat,

        @Schema(description = "Longitud del epicentro de los reportes", example = "-92.9250")
        @NotNull Double centerLon,

        @Schema(description = "Cantidad de reportes simulados (3 o más activan consenso inmediato)", example = "3")
        @Min(1) @Max(10) int count,

        @Schema(description = "Radio de dispersión de los reportes en metros", example = "80.0")
        @Min(10) @Max(300) double radiusMeters
) {}
