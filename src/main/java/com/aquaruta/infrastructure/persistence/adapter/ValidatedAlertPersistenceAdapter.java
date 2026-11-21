package com.aquaruta.infrastructure.persistence.adapter;

import com.aquaruta.domain.model.ReportType;
import com.aquaruta.domain.model.ValidatedAlert;
import com.aquaruta.domain.repository.ValidatedAlertRepositoryPort;
import com.aquaruta.infrastructure.config.SpatialConfig;
import com.aquaruta.infrastructure.persistence.entity.ValidatedAlertEntity;
import com.aquaruta.infrastructure.persistence.repository.SpringDataValidatedAlertRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para alertas validadas en PostgreSQL/PostGIS.
 */
@Component
public class ValidatedAlertPersistenceAdapter implements ValidatedAlertRepositoryPort {

    private final SpringDataValidatedAlertRepository alertRepository;

    public ValidatedAlertPersistenceAdapter(SpringDataValidatedAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public ValidatedAlert save(ValidatedAlert alert) {
        ValidatedAlertEntity entity = toEntity(alert);
        return toDomain(alertRepository.save(entity));
    }

    @Override
    public Optional<ValidatedAlert> findById(String id) {
        return alertRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ValidatedAlert> findAllActive() {
        return alertRepository.findByStatus("ACTIVA").stream().map(this::toDomain).toList();
    }

    @Override
    public List<ValidatedAlert> findActiveNearby(double latitude, double longitude, double radiusMeters, ReportType type) {
        return alertRepository.findActiveNearbyNative(latitude, longitude, radiusMeters, type)
                .stream().map(this::toDomain).toList();
    }

    private ValidatedAlertEntity toEntity(ValidatedAlert d) {
        return new ValidatedAlertEntity(
                d.getId(),
                d.getAlertType(),
                d.getCentroidLat(),
                d.getCentroidLon(),
                d.getRadiusMeters(),
                d.getReportCount(),
                d.getConfirmedAt(),
                d.getStatus(),
                SpatialConfig.createPoint(d.getCentroidLat(), d.getCentroidLon())
        );
    }

    private ValidatedAlert toDomain(ValidatedAlertEntity e) {
        return new ValidatedAlert(
                e.getId(),
                e.getAlertType(),
                e.getCentroidLat(),
                e.getCentroidLon(),
                e.getRadiusMeters(),
                e.getReportCount(),
                e.getConfirmedAt(),
                e.getStatus()
        );
    }
}
