package com.aquaruta.domain.repository;

import com.aquaruta.domain.model.ReportType;
import com.aquaruta.domain.model.ValidatedAlert;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para las alertas hídricas validadas por consenso.
 *
 * @author AquaRuta VSA Engineering Team
 */
public interface ValidatedAlertRepositoryPort {

    ValidatedAlert save(ValidatedAlert alert);

    Optional<ValidatedAlert> findById(String id);

    List<ValidatedAlert> findAllActive();

    /**
     * Localiza alertas activas que contengan o se encuentren a menos del radio especificado.
     *
     * @param latitude     Latitud
     * @param longitude    Longitud
     * @param radiusMeters Radio en metros
     * @param type         Tipo de alerta
     * @return Alertas activas en la proximidad
     */
    List<ValidatedAlert> findActiveNearby(double latitude, double longitude, double radiusMeters, ReportType type);
}
