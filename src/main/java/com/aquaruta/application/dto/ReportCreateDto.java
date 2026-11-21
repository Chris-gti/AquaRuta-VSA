package com.aquaruta.application.dto;

import com.aquaruta.domain.model.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para creación de nuevo reporte ciudadano.
 */
@Schema(description = "Datos de entrada para emitir un reporte ciudadano de agua o vialidad")
public record ReportCreateDto(
        @Schema(description = "Tipo de incidencia", example = "CORTE_AGUA")
        @NotNull ReportType reportType,

        @Schema(description = "Descripción ciudadana del incidente", example = "Sin agua potable en la privada desde las 3pm")
        @NotBlank String description,

        @Schema(description = "Latitud del incidente", example = "17.9890")
        @NotNull Double latitude,

        @Schema(description = "Longitud del incidente", example = "-92.9250")
        @NotNull Double longitude
) {}
