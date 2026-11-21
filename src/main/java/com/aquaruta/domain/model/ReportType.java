package com.aquaruta.domain.model;

/**
 * Tipología de reportes ciudadanos recolectados en Villahermosa.
 *
 * @author AquaRuta VSA Engineering Team
 */
public enum ReportType {
    CORTE_AGUA("Corte total de suministro de agua potable"),
    BAJA_PRESION("Baja presión severa en la red domiciliaria"),
    CALLE_INUNDADA("Anegamiento o acumulación de agua que impide el tránsito"),
    ALCANTARILLA_TAPADA("Drenaje pluvial o alcantarilla obstruida con riesgo de desborde");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
