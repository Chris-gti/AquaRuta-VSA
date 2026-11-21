package com.aquaruta.infrastructure.persistence.repository;

import com.aquaruta.domain.model.ReportType;
import com.aquaruta.infrastructure.persistence.entity.ValidatedAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para alertas validadas con consultas espaciales.
 */
@Repository
public interface SpringDataValidatedAlertRepository extends JpaRepository<ValidatedAlertEntity, String> {

    List<ValidatedAlertEntity> findByStatus(String status);

    @Query(value = """
        SELECT * FROM validated_alerts a 
        WHERE a.status = 'ACTIVA' 
          AND a.alert_type = :#{#type.name()} 
          AND ST_DWithin(a.centroid_geom::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :radiusMeters)
        """, nativeQuery = true)
    List<ValidatedAlertEntity> findActiveNearbyNative(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radiusMeters") double radiusMeters,
            @Param("type") ReportType type);
}
