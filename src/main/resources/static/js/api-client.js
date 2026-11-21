/**
 * Cliente HTTP para comunicación asíncrona con la API REST de AquaRuta VSA.
 */
class ApiClient {
    constructor(baseUrl = '') {
        this.baseUrl = baseUrl;
    }

    async getRoute(startLat, startLon, endLat, endLon, resilientMode = true) {
        const url = `${this.baseUrl}/api/v1/routes?startLat=${startLat}&startLon=${startLon}&endLat=${endLat}&endLon=${endLon}&resilientMode=${resilientMode}`;
        const res = await fetch(url);
        if (!res.ok) {
            if (res.status === 404) return null;
            throw new Error(`Error al calcular ruta: ${res.statusText}`);
        }
        return await res.json();
    }

    async getReports() {
        const res = await fetch(`${this.baseUrl}/api/v1/reports`);
        if (!res.ok) throw new Error('Error al consultar reportes');
        return await res.json();
    }

    async getActiveAlerts() {
        const res = await fetch(`${this.baseUrl}/api/v1/reports/alerts`);
        if (!res.ok) throw new Error('Error al consultar alertas activas');
        return await res.json();
    }

    async createReport(reportType, description, latitude, longitude) {
        const res = await fetch(`${this.baseUrl}/api/v1/reports`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ reportType, description, latitude, longitude })
        });
        if (!res.ok) throw new Error('Error al registrar reporte ciudadano');
        return await res.json();
    }

    async getWeatherCells() {
        const res = await fetch(`${this.baseUrl}/api/v1/weather/cells`);
        if (!res.ok) throw new Error('Error al consultar cuadrantes climáticos');
        return await res.json();
    }

    async injectRain(quadrantId, precipitationMmH) {
        const res = await fetch(`${this.baseUrl}/api/v1/simulation/rain`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ quadrantId, precipitationMmH })
        });
        if (!res.ok) throw new Error('Error al inyectar lluvia simulada');
        return await res.json();
    }

    async triggerBurst(reportType, centerLat, centerLon, count = 3, radiusMeters = 80) {
        const res = await fetch(`${this.baseUrl}/api/v1/simulation/reports/burst`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ reportType, centerLat, centerLon, count, radiusMeters })
        });
        if (!res.ok) throw new Error('Error al disparar ráfaga de reportes');
        return await res.json();
    }

    async resetSimulation() {
        const res = await fetch(`${this.baseUrl}/api/v1/simulation/reset`, { method: 'POST' });
        if (!res.ok) throw new Error('Error al restablecer simulación');
        return await res.json();
    }
}

export const apiClient = new ApiClient();
