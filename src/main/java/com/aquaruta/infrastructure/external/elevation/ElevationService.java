package com.aquaruta.infrastructure.external.elevation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio de altimetría calibrado para la topografía de Villahermosa Centro (3 a 15 msnm).
 */
@Service
public class ElevationService {

    private static final Logger log = LoggerFactory.getLogger(ElevationService.class);

    /**
     * Calcula la altitud en m.s.n.m. para una coordenada en Villahermosa.
     * Modela cotas críticas bajas en Gaviotas y márgenes de los ríos Grijalva y Carrizal.
     */
    public double getElevationForCoordinate(double latitude, double longitude) {
        // Gaviotas y margen este del Río Grijalva (Zonas bajas críticas: 3.5 a 5.0 msnm)
        if (longitude > -92.9200) {
            double factor = Math.abs(latitude - 17.9850) * 15.0;
            return Math.round((3.8 + Math.min(2.0, factor)) * 10.0) / 10.0;
        }

        // Centro Histórico y Malecón (Cotas bajas: 4.5 a 6.5 msnm)
        if (longitude >= -92.9350 && longitude <= -92.9200 && latitude <= 18.0000) {
            double factor = Math.abs(latitude - 17.9900) * 10.0;
            return Math.round((4.8 + Math.min(2.2, factor)) * 10.0) / 10.0;
        }

        // Tabasco 2000 y zonas altas de Atasta / Tamulté (Cotas medias-altas: 9.0 a 14.5 msnm)
        if (longitude < -92.9350) {
            double distFromRiver = Math.abs(latitude - 18.0100) * 20.0;
            return Math.round((9.5 + Math.min(5.0, distFromRiver)) * 10.0) / 10.0;
        }

        // Cota promedio urbana
        return 7.5;
    }
}
