-- ============================================================================
-- AQUARUTA VSA: Base de Datos Geoespacial PostGIS
-- Sistema de Enrutamiento Resiliente y Monitoreo Hídrico para Villahermosa, Tabasco
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS postgis;

-- 1. Nodos Viales
CREATE TABLE IF NOT EXISTS road_nodes (
    id BIGINT PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    elevation_msnm DOUBLE PRECISION NOT NULL DEFAULT 8.0,
    geom geometry(Point, 4326) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_road_nodes_geom ON road_nodes USING GIST (geom);

-- 2. Tramos / Aristas Viales
CREATE TABLE IF NOT EXISTS road_edges (
    id VARCHAR(64) PRIMARY KEY,
    source_node_id BIGINT NOT NULL REFERENCES road_nodes(id) ON DELETE CASCADE,
    target_node_id BIGINT NOT NULL REFERENCES road_nodes(id) ON DELETE CASCADE,
    street_name VARCHAR(255) DEFAULT 'Sin Nombre',
    highway_type VARCHAR(64) DEFAULT 'unclassified',
    length_meters DOUBLE PRECISION NOT NULL,
    oneway BOOLEAN DEFAULT FALSE,
    maxspeed INTEGER DEFAULT 40,
    geom geometry(LineString, 4326) NOT NULL,
    base_risk_factor DOUBLE PRECISION DEFAULT 1.0
);

CREATE INDEX IF NOT EXISTS idx_road_edges_geom ON road_edges USING GIST (geom);
CREATE INDEX IF NOT EXISTS idx_road_edges_source ON road_edges (source_node_id);
CREATE INDEX IF NOT EXISTS idx_road_edges_target ON road_edges (target_node_id);

-- 3. Células de la Cuadrícula Meteorológica
CREATE TABLE IF NOT EXISTS weather_cells (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    center_lat DOUBLE PRECISION NOT NULL,
    center_lon DOUBLE PRECISION NOT NULL,
    precipitation_mm_h DOUBLE PRECISION DEFAULT 0.0,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    geom geometry(Polygon, 4326) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_weather_cells_geom ON weather_cells USING GIST (geom);

-- 4. Reportes Ciudadanos
CREATE TABLE IF NOT EXISTS citizen_reports (
    id VARCHAR(64) PRIMARY KEY,
    report_type VARCHAR(32) NOT NULL,
    description TEXT,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    geom geometry(Point, 4326) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(32) DEFAULT 'PENDIENTE_VERIFICACION',
    cluster_id VARCHAR(64)
);

CREATE INDEX IF NOT EXISTS idx_citizen_reports_geom ON citizen_reports USING GIST (geom);
CREATE INDEX IF NOT EXISTS idx_citizen_reports_created ON citizen_reports (created_at);
CREATE INDEX IF NOT EXISTS idx_citizen_reports_status ON citizen_reports (status);
CREATE INDEX IF NOT EXISTS idx_citizen_reports_type ON citizen_reports (report_type);

-- 5. Alertas Validadas por Consenso Espacio-Temporal
CREATE TABLE IF NOT EXISTS validated_alerts (
    id VARCHAR(64) PRIMARY KEY,
    alert_type VARCHAR(32) NOT NULL,
    centroid_lat DOUBLE PRECISION NOT NULL,
    centroid_lon DOUBLE PRECISION NOT NULL,
    centroid_geom geometry(Point, 4326) NOT NULL,
    radius_meters DOUBLE PRECISION DEFAULT 200.0,
    report_count INT NOT NULL DEFAULT 3,
    confirmed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(32) DEFAULT 'ACTIVA'
);

CREATE INDEX IF NOT EXISTS idx_validated_alerts_geom ON validated_alerts USING GIST (centroid_geom);
CREATE INDEX IF NOT EXISTS idx_validated_alerts_status ON validated_alerts (status);

-- Semilla inicial de Cuadrantes Meteorológicos
INSERT INTO weather_cells (id, name, center_lat, center_lon, precipitation_mm_h, geom)
VALUES 
    (
        'QUAD_NORTE', 
        'Cuadrante Norte - Tabasco 2000 / Carrizal', 
        18.0025, 
        -92.9450, 
        0.0,
        ST_GeomFromText('POLYGON((-92.9650 17.9900, -92.9350 17.9900, -92.9350 18.0150, -92.9650 18.0150, -92.9650 17.9900))', 4326)
    ),
    (
        'QUAD_CENTRO', 
        'Cuadrante Centro - Zona Luz / Centro Histórico', 
        17.9900, 
        -92.9250, 
        0.0,
        ST_GeomFromText('POLYGON((-92.9350 17.9800, -92.9150 17.9800, -92.9150 18.0050, -92.9350 18.0050, -92.9350 17.9800))', 4326)
    ),
    (
        'QUAD_GAVIOTAS', 
        'Cuadrante Este - Gaviotas / Margen Río Grijalva', 
        17.9850, 
        -92.9100, 
        0.0,
        ST_GeomFromText('POLYGON((-92.9150 17.9700, -92.8950 17.9700, -92.8950 18.0000, -92.9150 18.0000, -92.9150 17.9700))', 4326)
    ),
    (
        'QUAD_SUR', 
        'Cuadrante Sur - Tamulté / Atasta / Carlos Pellicer', 
        17.9750, 
        -92.9450, 
        0.0,
        ST_GeomFromText('POLYGON((-92.9650 17.9650, -92.9300 17.9650, -92.9300 17.9900, -92.9650 17.9900, -92.9650 17.9650))', 4326)
    )
ON CONFLICT (id) DO NOTHING;
