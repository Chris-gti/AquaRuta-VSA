package com.aquaruta.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoPointTest {

    @Test
    @DisplayName("Debe calcular correctamente la distancia Haversine entre Tabasco 2000 y Centro Histórico")
    void testHaversineDistanceBetweenPointsInVillahermosa() {
        // Tabasco 2000 (Pirámide de Pemex)
        GeoPoint tabasco2000 = new GeoPoint(17.9985, -92.9372);
        // Catedral del Señor de Tabasco (Centro)
        GeoPoint catedral = new GeoPoint(17.9922, -92.9305);

        double distanceMeters = tabasco2000.distanceTo(catedral);

        // La distancia real en línea recta es de aproximadamente 980 - 1050 metros
        assertTrue(distanceMeters > 900.0 && distanceMeters < 1200.0,
                "La distancia calculada debe estar en el rango esperado (~1000m), fue: " + distanceMeters);
    }

    @Test
    @DisplayName("Distancia a sí mismo debe ser exactamente 0")
    void testDistanceToSelfIsZero() {
        GeoPoint p = new GeoPoint(17.9890, -92.9280);
        assertEquals(0.0, p.distanceTo(p), 0.0001);
    }

    @Test
    @DisplayName("Debe rechazar coordenadas fuera de rango")
    void testInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(95.0, -92.0));
        assertThrows(IllegalArgumentException.class, () -> new GeoPoint(17.0, -190.0));
    }
}
