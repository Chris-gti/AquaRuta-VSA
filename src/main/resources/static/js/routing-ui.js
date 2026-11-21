/**
 * Controlador de interfaz de usuario para cálculo y visualización de rutas viales.
 */
import { apiClient } from './api-client.js';

export class RoutingUI {
    constructor(mapManager, showToast) {
        this.mapManager = mapManager;
        this.showToast = showToast;

        this.origin = null;
        this.destination = null;
        this.resilientMode = true;
        this.selectingTarget = 'origin'; // 'origin' o 'destination'

        this.inputOrigin = document.getElementById('input-origin');
        this.inputDest = document.getElementById('input-dest');
        this.btnCalculate = document.getElementById('btn-calculate-route');
        this.metricsCard = document.getElementById('route-metrics');

        // Presets rápidos de Villahermosa
        this.presets = {
            tabasco2000: { name: 'Tabasco 2000', lat: 17.9985, lon: -92.9372 },
            centro: { name: 'Zona Luz / Centro', lat: 17.9890, lon: -92.9250 },
            gaviotas: { name: 'Gaviotas Norte', lat: 17.9835, lon: -92.9090 },
            tamulte: { name: 'Tamulté', lat: 17.9780, lon: -92.9350 }
        };
    }

    init() {
        this.bindEvents();
        // Cargar ruta inicial de demostración: Tabasco 2000 a Gaviotas Norte
        this.setOrigin(this.presets.tabasco2000.lat, this.presets.tabasco2000.lon);
        this.setDestination(this.presets.gaviotas.lat, this.presets.gaviotas.lon);
        this.calculateRoute();
    }

    bindEvents() {
        // Clics en inputs para seleccionar qué punto colocar en el mapa
        if (this.inputOrigin) {
            this.inputOrigin.addEventListener('focus', () => this.selectingTarget = 'origin');
        }
        if (this.inputDest) {
            this.inputDest.addEventListener('focus', () => this.selectingTarget = 'destination');
        }

        // Botones de presets
        document.querySelectorAll('.preset-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const key = e.target.dataset.preset;
                const p = this.presets[key];
                if (p) {
                    if (this.selectingTarget === 'origin') {
                        this.setOrigin(p.lat, p.lon);
                        this.selectingTarget = 'destination';
                    } else {
                        this.setDestination(p.lat, p.lon);
                    }
                    this.calculateRoute();
                }
            });
        });

        // Botón calcular
        if (this.btnCalculate) {
            this.btnCalculate.addEventListener('click', () => this.calculateRoute());
        }

        // Selector de modo de enrutamiento (Header)
        const btnResilient = document.getElementById('mode-resilient');
        const btnShortest = document.getElementById('mode-shortest');

        if (btnResilient && btnShortest) {
            btnResilient.addEventListener('click', () => {
                this.resilientMode = true;
                btnResilient.classList.add('active');
                btnShortest.classList.remove('active');
                this.showToast('Modo de Evasión de Inundaciones Activado', 'info');
                this.calculateRoute();
            });

            btnShortest.addEventListener('click', () => {
                this.resilientMode = false;
                btnShortest.classList.add('active');
                btnResilient.classList.remove('active');
                this.showToast('Modo Ruta Más Corta (Estándar) Activado', 'info');
                this.calculateRoute();
            });
        }
    }

    handleMapClick(lat, lon) {
        if (this.selectingTarget === 'origin') {
            this.setOrigin(lat, lon);
            this.selectingTarget = 'destination';
        } else {
            this.setDestination(lat, lon);
        }
        this.calculateRoute();
    }

    setOrigin(lat, lon) {
        this.origin = { lat, lon };
        if (this.inputOrigin) {
            this.inputOrigin.value = `${lat.toFixed(4)}, ${lon.toFixed(4)}`;
        }
        this.mapManager.setOriginMarker(lat, lon, (newLat, newLon) => {
            this.origin = { lat: newLat, lon: newLon };
            if (this.inputOrigin) this.inputOrigin.value = `${newLat.toFixed(4)}, ${newLon.toFixed(4)}`;
            this.calculateRoute();
        });
    }

    setDestination(lat, lon) {
        this.destination = { lat, lon };
        if (this.inputDest) {
            this.inputDest.value = `${lat.toFixed(4)}, ${lon.toFixed(4)}`;
        }
        this.mapManager.setDestinationMarker(lat, lon, (newLat, newLon) => {
            this.destination = { lat: newLat, lon: newLon };
            if (this.inputDest) this.inputDest.value = `${newLat.toFixed(4)}, ${newLon.toFixed(4)}`;
            this.calculateRoute();
        });
    }

    async calculateRoute() {
        if (!this.origin || !this.destination) return;

        try {
            const data = await apiClient.getRoute(
                this.origin.lat,
                this.origin.lon,
                this.destination.lat,
                this.destination.lon,
                this.resilientMode
            );

            if (data && data.pathCoordinates && data.pathCoordinates.length > 0) {
                this.mapManager.renderRoute(data.pathCoordinates, this.resilientMode);
                this.updateMetricsUI(data);

                if (data.blockedStreetsAvoided > 0) {
                    this.showToast(`Ruta A* desvió el trayecto evitando ${data.blockedStreetsAvoided} tramos con riesgo de inundación`, 'warning');
                }
            } else {
                this.showToast('No se encontró ruta transitable entre los puntos seleccionados', 'warning');
            }
        } catch (e) {
            console.error(e);
            this.showToast('Error al calcular ruta con el servidor', 'warning');
        }
    }

    updateMetricsUI(data) {
        if (!this.metricsCard) return;
        this.metricsCard.style.display = 'block';

        const distKm = (data.totalDistanceMeters / 1000.0).toFixed(2);
        document.getElementById('metric-dist').textContent = `${distKm} km`;
        document.getElementById('metric-time').textContent = `${data.estimatedTimeMinutes.toFixed(1)} min`;
        document.getElementById('metric-elev').textContent = `${data.averageElevationMsnm.toFixed(1)} m`;

        const avoidedEl = document.getElementById('metric-avoided');
        avoidedEl.textContent = `${data.blockedStreetsAvoided}`;
        if (data.blockedStreetsAvoided > 0) {
            avoidedEl.className = 'metric-value metric-avoided';
        } else {
            avoidedEl.className = 'metric-value metric-highlight';
        }
    }
}
