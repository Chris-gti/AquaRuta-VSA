package com.aquaruta.domain.model;

/**
 * Estados del ciclo de vida de un reporte ciudadano según el consenso espacio-temporal.
 *
 * @author AquaRuta VSA Engineering Team
 */
public enum ReportStatus {
    PENDIENTE_VERIFICACION("Reporte individual pendiente de validación por quórum"),
    CONFIRMADO("Consenso alcanzado por múltiples reportes en la ventana espacio-temporal"),
    RESUELTO("Incidencia atendida o mitigada"),
    DESCARTADO("Falsa alarma o reporte expirado");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
