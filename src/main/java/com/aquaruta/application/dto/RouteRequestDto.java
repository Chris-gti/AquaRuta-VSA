package com.aquaruta.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para solicitud de cálculo de ruta vial.
 */
@Schema(description = "Parámetros de entrada para el cálculo de ruta óptima en Villahermosa")
public record RouteRequestDto(
        @Schema(description = "Latitud de origen", example = "17.9985")
        @NotNull Double startLat,

        @Schema(description = "Longitud de origen", example = "-92.9372")
        @NotNull Double startLon,

        @Schema(description = "Latitud de destino", example = "17.9835")
        @NotNull Double endLat,

        @Schema(description = "Longitud de destino", example = "-92.9090")
        @NotNull Double endLon,

        @Schema(description = "Activar algoritmo de evasión de inundaciones y cotas bajas", example = "true")
        Boolean resilientMode
) {
    public boolean isResilientMode() {
        return resilientMode == null || resilientMode;
    }
}
