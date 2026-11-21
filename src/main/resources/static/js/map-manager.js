/**
 * Gestor del mapa interactivo Leaflet para Villahermosa Centro.
 */
export class MapManager {
    constructor(mapContainerId = 'map') {
        this.mapContainerId = mapContainerId;
        this.map = null;

        // Capas cartográficas
        this.routesLayer = null;
        this.quadrantsLayer = null;
        this.reportsLayer = null;
        this.alertsLayer = null;
        this.tempMarkersLayer = null;

        // Marcadores de origen y destino
        this.markerOrigin = null;
        this.markerDestination = null;

        // Bounding Box de Villahermosa Centro
        this.centerCoords = [17.9890, -92.9280];
        this.clickCallback = null;
    }

    init() {
        this.map = L.map(this.mapContainerId, {
            center: this.centerCoords,
            zoom: 14,
            zoomControl: false
        });

        L.control.zoom({ position: 'bottomleft' }).addTo(this.map);

        // Capa base oficial OpenStreetMap estándar (sin marcas de agua)
        const osmStandard = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
            maxZoom: 19
        }).addTo(this.map);

        // Inicializar grupos de capas
        this.quadrantsLayer = L.layerGroup().addTo(this.map);
        this.routesLayer = L.layerGroup().addTo(this.map);
        this.reportsLayer = L.layerGroup().addTo(this.map);
        this.alertsLayer = L.layerGroup().addTo(this.map);
        this.tempMarkersLayer = L.layerGroup().addTo(this.map);

        // Evento de clic en el mapa
        this.map.on('click', (e) => {
            if (this.clickCallback) {
                this.clickCallback(e.latlng.lat, e.latlng.lng);
            }
        });

        return this;
    }

    setMapClickHandler(callback) {
        this.clickCallback = callback;
    }

    setOriginMarker(lat, lon, onDragCallback) {
        if (this.markerOrigin) {
            this.markerOrigin.setLatLng([lat, lon]);
        } else {
            const icon = L.divIcon({
                className: 'custom-pin-a',
                html: '<div style="background:#2563EB;color:white;width:24px;height:24px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:12px;border:2px solid white;box-shadow:0 2px 6px rgba(0,0,0,0.3);">A</div>',
                iconSize: [24, 24],
                iconAnchor: [12, 12]
            });
            this.markerOrigin = L.marker([lat, lon], { icon, draggable: true }).addTo(this.map);
            if (onDragCallback) {
                this.markerOrigin.on('dragend', (e) => {
                    const pos = e.target.getLatLng();
                    onDragCallback(pos.lat, pos.lng);
                });
            }
        }
    }

    setDestinationMarker(lat, lon, onDragCallback) {
        if (this.markerDestination) {
            this.markerDestination.setLatLng([lat, lon]);
        } else {
            const icon = L.divIcon({
                className: 'custom-pin-b',
                html: '<div style="background:#EF4444;color:white;width:24px;height:24px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:12px;border:2px solid white;box-shadow:0 2px 6px rgba(0,0,0,0.3);">B</div>',
                iconSize: [24, 24],
                iconAnchor: [12, 12]
            });
            this.markerDestination = L.marker([lat, lon], { icon, draggable: true }).addTo(this.map);
            if (onDragCallback) {
                this.markerDestination.on('dragend', (e) => {
                    const pos = e.target.getLatLng();
                    onDragCallback(pos.lat, pos.lng);
                });
            }
        }
    }

    renderRoute(pathCoordinates, isResilientMode = true) {
        this.routesLayer.clearLayers();
        if (!pathCoordinates || pathCoordinates.length < 2) return;

        const color = isResilientMode ? '#2563EB' : '#64748B';
        const polyline = L.polyline(pathCoordinates, {
            color: color,
            weight: 6,
            opacity: 0.9,
            lineJoin: 'round'
        }).addTo(this.routesLayer);

        // Línea de contorno interior para realce de ingeniería
        L.polyline(pathCoordinates, {
            color: '#FFFFFF',
            weight: 2,
            opacity: 0.6,
            lineJoin: 'round'
        }).addTo(this.routesLayer);

        this.map.fitBounds(polyline.getBounds(), { padding: [50, 50], maxZoom: 16 });
    }

    renderQuadrants(quadrants) {
        this.quadrantsLayer.clearLayers();
        if (!quadrants) return;

        quadrants.forEach(q => {
            const rain = q.precipitationMmH || 0;
            let fillColor = '#3B82F6';
            let fillOpacity = 0.05;

            if (rain > 70) {
                fillColor = '#EF4444';
                fillOpacity = 0.25;
            } else if (rain > 35) {
                fillColor = '#F59E0B';
                fillOpacity = 0.20;
            } else if (rain > 0) {
                fillColor = '#10B981';
                fillOpacity = 0.12;
            }

            // Coordenadas aproximadas del cuadrante
            const bounds = this.getQuadrantBounds(q.id);
            if (bounds) {
                const rect = L.rectangle(bounds, {
                    color: fillColor,
                    weight: 1.5,
                    fillColor: fillColor,
                    fillOpacity: fillOpacity,
                    dashArray: rain > 40 ? '4, 4' : null
                }).addTo(this.quadrantsLayer);

                rect.bindTooltip(`<b>${q.name}</b><br>Lluvia: <b>${rain.toFixed(1)} mm/h</b>`, {
                    permanent: false,
                    direction: 'center'
                });
            }
        });
    }

    getQuadrantBounds(quadrantId) {
        switch (quadrantId) {
            case 'QUAD_NORTE': return [[17.9900, -92.9650], [18.0150, -92.9350]];
            case 'QUAD_CENTRO': return [[17.9800, -92.9350], [18.0050, -92.9150]];
            case 'QUAD_GAVIOTAS': return [[17.9700, -92.9150], [18.0000, -92.8950]];
            case 'QUAD_SUR': return [[17.9650, -92.9650], [17.9900, -92.9300]];
            default: return null;
        }
    }

    renderReports(reports) {
        this.reportsLayer.clearLayers();
        if (!reports) return;

        reports.forEach(r => {
            const isConfirmed = r.status === 'CONFIRMADO';
            const color = isConfirmed ? '#EF4444' : '#F59E0B';

            const marker = L.circleMarker([r.latitude, r.longitude], {
                radius: 6,
                fillColor: color,
                color: '#FFFFFF',
                weight: 1.5,
                fillOpacity: 0.9
            }).addTo(this.reportsLayer);

            marker.bindPopup(`
                <div style="font-size:12px; font-family:sans-serif;">
                    <b style="color:${color};">${r.reportType}</b><br>
                    <span>${r.description}</span><br>
                    <small style="color:#64748B;">Estado: <b>${r.status}</b></small>
                </div>
            `);
        });
    }

    renderAlerts(alerts) {
        this.alertsLayer.clearLayers();
        if (!alerts) return;

        alerts.forEach(a => {
            const circle = L.circle([a.centroidLat, a.centroidLon], {
                radius: a.radiusMeters || 200,
                color: '#EF4444',
                weight: 2,
                fillColor: '#EF4444',
                fillOpacity: 0.18,
                dashArray: '6, 6'
            }).addTo(this.alertsLayer);

            circle.bindPopup(`
                <div style="font-size:12px; font-family:sans-serif;">
                    <b style="color:#EF4444;">ALERTA CONFIRMADA POR QUÓRUM</b><br>
                    <span>Tipo: <b>${a.alertType}</b></span><br>
                    <span>Reportes validados: <b>${a.reportCount}</b> en radio de ${a.radiusMeters}m</span><br>
                    <small style="color:#64748B;">Alerta Activa</small>
                </div>
            `);
        });
    }

    showTempRadius(lat, lon, radiusMeters = 200) {
        this.tempMarkersLayer.clearLayers();
        L.circle([lat, lon], {
            radius: radiusMeters,
            color: '#3B82F6',
            weight: 1,
            fillColor: '#3B82F6',
            fillOpacity: 0.12,
            dashArray: '4, 4'
        }).addTo(this.tempMarkersLayer);
    }

    clearTempRadius() {
        this.tempMarkersLayer.clearLayers();
    }
}
