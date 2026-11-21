package com.aquaruta.infrastructure.external.elevation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationServiceTest {

    private ElevationService elevationService;

    @BeforeEach
    void setUp() {
        elevationService = new ElevationService();
    }

    @Test
    @DisplayName("Gaviotas y margen este del Río Grijalva debe tener cota baja (< 6 msnm)")
    void testGaviotasLowElevation() {
        // Coordenadas en Sector Gaviotas Norte
        double elev = elevationService.getElevationForCoordinate(17.9835, -92.9090);
        assertTrue(elev < 6.0, "Gaviotas debe ser zona baja (< 6.0 msnm), fue: " + elev);
    }

    @Test
    @DisplayName("Tabasco 2000 debe tener cota más alta (> 9 msnm)")
    void testTabasco2000HighElevation() {
        // Coordenadas en Tabasco 2000
        double elev = elevationService.getElevationForCoordinate(17.9985, -92.9372);
        assertTrue(elev >= 9.0, "Tabasco 2000 debe situarse en terreno más elevado (>= 9.0 msnm), fue: " + elev);
    }
}
