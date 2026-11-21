package com.aquaruta.infrastructure.config;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de factoría geométrica JTS para SRID 4326 (WGS84).
 */
@Configuration
public class SpatialConfig {

    private static final int SRID = 4326;
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), SRID);

    @Bean
    public GeometryFactory geometryFactory() {
        return GEOMETRY_FACTORY;
    }

    public static Point createPoint(double latitude, double longitude) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
    }

    public static LineString createLineString(List<Coordinate> coordinates) {
        return GEOMETRY_FACTORY.createLineString(coordinates.toArray(new Coordinate[0]));
    }
}
