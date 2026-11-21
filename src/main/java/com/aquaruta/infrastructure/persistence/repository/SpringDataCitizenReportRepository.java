package com.aquaruta.infrastructure.persistence.repository;

import com.aquaruta.domain.model.ReportStatus;
import com.aquaruta.domain.model.ReportType;
import com.aquaruta.infrastructure.persistence.entity.CitizenReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repositorio Spring Data JPA para reportes ciudadanos con soporte PostGIS.
 */
@Repository
public interface SpringDataCitizenReportRepository extends JpaRepository<CitizenReportEntity, String> {

    List<CitizenReportEntity> findByReportTypeAndCreatedAtAfterAndStatusNot(
            ReportType reportType, Instant since, ReportStatus excludeStatus);

    @Query(value = """
        SELECT * FROM citizen_reports r 
        WHERE r.report_type = :#{#type.name()} 
          AND r.created_at >= :since 
          AND ST_DWithin(r.geom::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :radiusMeters)
        """, nativeQuery = true)
    List<CitizenReportEntity> findNearbyReportsNative(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radiusMeters") double radiusMeters,
            @Param("type") ReportType type,
            @Param("since") Instant since);

    @Modifying
    @Query("UPDATE CitizenReportEntity r SET r.status = :status, r.clusterId = :clusterId WHERE r.id = :id")
    void updateStatusAndCluster(@Param("id") String id, @Param("status") ReportStatus status, @Param("clusterId") String clusterId);
}
