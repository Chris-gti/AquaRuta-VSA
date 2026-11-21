package com.aquaruta.domain.model;

/**
 * Objeto de valor (Value Object) inmutable que encapsula una coordenada geográfica en WGS84.
 * Incluye cálculo de distancia geodésica mediante la fórmula del semiverseno (Haversine).
 *
 * @param latitude  Latitud en grados decimales [-90.0, 90.0]
 * @param longitude Longitud en grados decimales [-180.0, 180.0]
 *
 * @author AquaRuta VSA Engineering Team
 */
public record GeoPoint(double latitude, double longitude) {

    private static final double EARTH_RADIUS_METERS = 6371000.0;

    public GeoPoint {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitud fuera de rango [-90, 90]: " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitud fuera de rango [-180, 180]: " + longitude);
        }
    }

    /**
     * Calcula la distancia en metros entre este punto y otro destino usando la fórmula de Haversine.
     *
     * @param other Coordenada destino
     * @return Distancia euclidiana sobre la esfera terrestre en metros
     */
    public double distanceTo(GeoPoint other) {
        if (other == null) {
            return Double.MAX_VALUE;
        }
        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude());
        double deltaLat = Math.toRadians(other.latitude() - this.latitude);
        double deltaLon = Math.toRadians(other.longitude() - this.longitude);

        double a = Math.sin(deltaLat / 2.0) * Math.sin(deltaLat / 2.0)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2.0) * Math.sin(deltaLon / 2.0);

        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return EARTH_RADIUS_METERS * c;
    }
}
