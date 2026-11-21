package com.aquaruta.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para inyección de lluvia artificial en el Modo Demo.
 */
@Schema(description = "Inyección de escenario de lluvia simulada en un cuadrante")
public record SimulationRainDto(
        @Schema(description = "ID del cuadrante (ej. QUAD_CENTRO, QUAD_GAVIOTAS, QUAD_NORTE, QUAD_SUR)", example = "QUAD_GAVIOTAS")
        @NotBlank String quadrantId,

        @Schema(description = "Precipitación simulada en mm/h (0=Seco, 40=Moderada, 80=Tormenta Severa)", example = "75.0")
        @NotNull @Min(0) Double precipitationMmH
) {}
