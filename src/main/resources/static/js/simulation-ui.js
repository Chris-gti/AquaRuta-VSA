/**
 * Controlador de interfaz de usuario para el Modo Simulación Hidrológica (Demo).
 */
import { apiClient } from './api-client.js';

export class SimulationUI {
    constructor(mapManager, routingUI, showToast) {
        this.mapManager = mapManager;
        this.routingUI = routingUI;
        this.showToast = showToast;

        this.panel = document.getElementById('panel-demo');
        this.toggleBtn = document.getElementById('btn-toggle-demo');
        this.closeBtn = document.getElementById('btn-close-demo');

        this.selectQuadrant = document.getElementById('sim-quadrant');
        this.sliderRain = document.getElementById('sim-rain-slider');
        this.valRain = document.getElementById('sim-rain-val');

        this.btnBurst = document.getElementById('btn-sim-burst');
        this.btnReset = document.getElementById('btn-sim-reset');
    }

    init() {
        this.bindEvents();
        this.refreshWeatherGrid();
    }

    bindEvents() {
        // Alternar visibilidad del panel de simulación
        if (this.toggleBtn && this.panel) {
            this.toggleBtn.addEventListener('click', () => {
                this.panel.classList.toggle('collapsed');
            });
        }
        if (this.closeBtn && this.panel) {
            this.closeBtn.addEventListener('click', () => {
                this.panel.classList.add('collapsed');
            });
        }

        // Slider de precipitación
        if (this.sliderRain && this.valRain) {
            this.sliderRain.addEventListener('input', (e) => {
                const val = parseFloat(e.target.value);
                this.valRain.textContent = `${val.toFixed(0)} mm/h`;
            });

            this.sliderRain.addEventListener('change', async (e) => {
                const val = parseFloat(e.target.value);
                const quad = this.selectQuadrant ? this.selectQuadrant.value : 'QUAD_GAVIOTAS';
                await this.applyRain(quad, val);
            });
        }

        // Botones de presets rápidos de lluvia
        document.querySelectorAll('.rain-preset-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const rain = parseFloat(e.target.dataset.rain);
                if (this.sliderRain && this.valRain) {
                    this.sliderRain.value = rain;
                    this.valRain.textContent = `${rain.toFixed(0)} mm/h`;
                }
                const quad = this.selectQuadrant ? this.selectQuadrant.value : 'QUAD_GAVIOTAS';
                await this.applyRain(quad, rain);
            });
        });

        // Botón de ráfaga de reportes (consenso)
        if (this.btnBurst) {
            this.btnBurst.addEventListener('click', async () => {
                try {
                    this.btnBurst.disabled = true;
                    this.btnBurst.textContent = 'Simulando consenso...';

                    // Disparar en Zona Luz / Centro (17.9890, -92.9250)
                    const burstData = await apiClient.triggerBurst('CORTE_AGUA', 17.9890, -92.9250, 3, 75.0);

                    // Refrescar reportes y alertas
                    const reports = await apiClient.getReports();
                    const alerts = await apiClient.getActiveAlerts();

                    this.mapManager.renderReports(reports);
                    this.mapManager.renderAlerts(alerts);

                    this.showToast('¡Consenso alcanzado! 3 reportes ciudadanos confirmaron corte de agua en radio de 200m', 'success');
                } catch (err) {
                    console.error(err);
                    this.showToast('Error al simular ráfaga de reportes', 'warning');
                } finally {
                    this.btnBurst.disabled = false;
                    this.btnBurst.textContent = 'Simular Ráfaga de Cortes (Quórum)';
                }
            });
        }

        // Botón de restablecer escenario
        if (this.btnReset) {
            this.btnReset.addEventListener('click', async () => {
                try {
                    await apiClient.resetSimulation();
                    if (this.sliderRain && this.valRain) {
                        this.sliderRain.value = 0;
                        this.valRain.textContent = '0 mm/h';
                    }
                    await this.refreshWeatherGrid();
                    await this.routingUI.calculateRoute();
                    this.showToast('Escenario restablecido a condiciones secas', 'info');
                } catch (err) {
                    console.error(err);
                }
            });
        }
    }

    async applyRain(quadrantId, precipitationMmH) {
        try {
            await apiClient.injectRain(quadrantId, precipitationMmH);
            await this.refreshWeatherGrid();
            // Recalcular ruta activa para observar desvío A* en tiempo real
            await this.routingUI.calculateRoute();
            this.showToast(`Lluvia inyectada: ${precipitationMmH} mm/h en ${quadrantId}. Grafos recalculados.`, 'info');
        } catch (err) {
            console.error(err);
            this.showToast('Error al inyectar lluvia simulada', 'warning');
        }
    }

    async refreshWeatherGrid() {
        try {
            const cells = await apiClient.getWeatherCells();
            this.mapManager.renderQuadrants(cells);
        } catch (err) {
            console.error('Error al actualizar cuadrícula meteorológica:', err);
        }
    }
}
