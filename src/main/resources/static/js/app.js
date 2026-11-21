/**
 * Módulo principal de arranque para AquaRuta VSA.
 */
import { apiClient } from './api-client.js';
import { MapManager } from './map-manager.js';
import { RoutingUI } from './routing-ui.js';
import { SimulationUI } from './simulation-ui.js';

// Manejador global de notificaciones Toast (evita apilamiento limpiando previos)
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    // Limpiar mensaje anterior si ya existe uno activo
    container.innerHTML = '';

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 250);
    }, 4000);
}

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Inicializar mapa
    const mapManager = new MapManager('map').init();

    // 2. Inicializar controlador de rutas
    const routingUI = new RoutingUI(mapManager, showToast);
    routingUI.init();

    // 3. Inicializar panel de simulación (Modo Demo)
    const simulationUI = new SimulationUI(mapManager, routingUI, showToast);
    simulationUI.init();

    // 4. Gestión de pestañas (Enrutamiento vs Reporte Ciudadano)
    let currentTab = 'tab-routing';
    const tabBtns = document.querySelectorAll('.tab-btn');
    tabBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            const target = e.target.dataset.tab;
            currentTab = target;

            tabBtns.forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');

            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            document.getElementById(target).classList.add('active');

            if (target === 'tab-report') {
                showToast('Haz clic en cualquier calle del mapa para fijar la ubicación del reporte', 'info');
            } else {
                mapManager.clearTempRadius();
            }
        });
    });

    // 5. Clic en mapa redirigido a la pestaña activa
    mapManager.setMapClickHandler((lat, lon) => {
        if (currentTab === 'tab-routing') {
            routingUI.handleMapClick(lat, lon);
        } else if (currentTab === 'tab-report') {
            const inputLat = document.getElementById('report-lat');
            const inputLon = document.getElementById('report-lon');
            if (inputLat) inputLat.value = lat.toFixed(4);
            if (inputLon) inputLon.value = lon.toFixed(4);
            mapManager.showTempRadius(lat, lon, 200);
        }
    });

    // 6. Envío de formulario de reporte ciudadano
    const btnSubmitReport = document.getElementById('btn-submit-report');
    if (btnSubmitReport) {
        btnSubmitReport.addEventListener('click', async () => {
            const type = document.getElementById('report-type').value;
            const desc = document.getElementById('report-desc').value;
            const lat = parseFloat(document.getElementById('report-lat').value);
            const lon = parseFloat(document.getElementById('report-lon').value);

            if (!desc || isNaN(lat) || isNaN(lon)) {
                showToast('Por favor selecciona una ubicación en el mapa e ingresa una descripción', 'warning');
                return;
            }

            try {
                btnSubmitReport.disabled = true;
                btnSubmitReport.textContent = 'Enviando...';

                await apiClient.createReport(type, desc, lat, lon);
                showToast('Reporte ciudadano registrado. Analizando consenso...', 'success');

                // Limpiar formulario y radio
                document.getElementById('report-desc').value = '';
                mapManager.clearTempRadius();

                // Recargar reportes y alertas
                const reports = await apiClient.getReports();
                const alerts = await apiClient.getActiveAlerts();
                mapManager.renderReports(reports);
                mapManager.renderAlerts(alerts);
            } catch (err) {
                console.error(err);
                showToast('Error al enviar el reporte ciudadano', 'warning');
            } finally {
                btnSubmitReport.disabled = false;
                btnSubmitReport.textContent = 'Enviar Reporte Ciudadano';
            }
        });
    }

    // 7. Carga inicial de datos desde backend
    try {
        const reports = await apiClient.getReports();
        const alerts = await apiClient.getActiveAlerts();
        mapManager.renderReports(reports);
        mapManager.renderAlerts(alerts);

        const statusDot = document.getElementById('conn-status-dot');
        const statusText = document.getElementById('conn-status-text');
        if (statusDot) statusDot.style.background = '#10B981';
        if (statusText) statusText.textContent = 'ONLINE (JGraphT RAM)';
    } catch (err) {
        console.warn('Conexión con el backend en espera:', err);
    }
});
