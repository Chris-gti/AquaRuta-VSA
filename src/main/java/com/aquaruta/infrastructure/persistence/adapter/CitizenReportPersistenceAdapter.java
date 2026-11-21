package com.aquaruta.infrastructure.persistence.adapter;

import com.aquaruta.domain.model.CitizenReport;
import com.aquaruta.domain.model.ReportStatus;
import com.aquaruta.domain.model.ReportType;
import com.aquaruta.domain.repository.CitizenReportRepositoryPort;
import com.aquaruta.infrastructure.config.SpatialConfig;
import com.aquaruta.infrastructure.persistence.entity.CitizenReportEntity;
import com.aquaruta.infrastructure.persistence.repository.SpringDataCitizenReportRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para reportes ciudadanos en PostgreSQL/PostGIS.
 */
@Component
public class CitizenReportPersistenceAdapter implements CitizenReportRepositoryPort {

    private final SpringDataCitizenReportRepository reportRepository;

    public CitizenReportPersistenceAdapter(SpringDataCitizenReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    @Transactional
    public CitizenReport save(CitizenReport report) {
        CitizenReportEntity entity = toEntity(report);
        return toDomain(reportRepository.save(entity));
    }

    @Override
    public Optional<CitizenReport> findById(String id) {
        return reportRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<CitizenReport> findAll() {
        return reportRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<CitizenReport> findRecentActiveReports(ReportType type, Instant since) {
        return reportRepository.findByReportTypeAndCreatedAtAfterAndStatusNot(type, since, ReportStatus.DESCARTADO)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<CitizenReport> findNearbyReports(double latitude, double longitude, double radiusMeters, ReportType type, Instant since) {
        return reportRepository.findNearbyReportsNative(latitude, longitude, radiusMeters, type, since)
                .stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void updateStatus(String reportId, ReportStatus status, String clusterId) {
        reportRepository.updateStatusAndCluster(reportId, status, clusterId);
    }

    private CitizenReportEntity toEntity(CitizenReport d) {
        return new CitizenReportEntity(
                d.getId(),
                d.getReportType(),
                d.getDescription(),
                d.getLatitude(),
                d.getLongitude(),
                d.getCreatedAt(),
                d.getStatus(),
                d.getClusterId(),
                SpatialConfig.createPoint(d.getLatitude(), d.getLongitude())
        );
    }

    private CitizenReport toDomain(CitizenReportEntity e) {
        return new CitizenReport(
                e.getId(),
                e.getReportType(),
                e.getDescription(),
                e.getLatitude(),
                e.getLongitude(),
                e.getCreatedAt(),
                e.getStatus(),
                e.getClusterId()
        );
    }
}
