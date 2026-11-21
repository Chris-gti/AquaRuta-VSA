package com.aquaruta.application.service;

import com.aquaruta.application.dto.ReportCreateDto;
import com.aquaruta.application.dto.ReportResponseDto;
import com.aquaruta.application.dto.ValidatedAlertResponseDto;
import com.aquaruta.domain.event.OutageEventPublisherPort;
import com.aquaruta.domain.event.OutageReportEvent;
import com.aquaruta.domain.model.CitizenReport;
import com.aquaruta.domain.model.ValidatedAlert;
import com.aquaruta.domain.repository.CitizenReportRepositoryPort;
import com.aquaruta.domain.repository.ValidatedAlertRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de aplicación para gestión y validación de reportes ciudadanos y cortes de agua.
 */
@Service
public class WaterOutageService {

    private static final Logger log = LoggerFactory.getLogger(WaterOutageService.class);

    private final CitizenReportRepositoryPort reportRepository;
    private final ValidatedAlertRepositoryPort alertRepository;
    private final OutageEventPublisherPort eventPublisher;

    public WaterOutageService(CitizenReportRepositoryPort reportRepository,
                              ValidatedAlertRepositoryPort alertRepository,
                              OutageEventPublisherPort eventPublisher) {
        this.reportRepository = reportRepository;
        this.alertRepository = alertRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registra un nuevo reporte ciudadano y dispara el evento para análisis de consenso.
     */
    @Transactional
    public ReportResponseDto createReport(ReportCreateDto dto) {
        CitizenReport report = CitizenReport.createNew(
                dto.reportType(),
                dto.description(),
                dto.latitude(),
                dto.longitude()
        );

        CitizenReport saved = reportRepository.save(report);
        eventPublisher.publishReportReceived(new OutageReportEvent(saved));

        return toDto(saved);
    }

    public List<ReportResponseDto> getAllReports() {
        return reportRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<ValidatedAlertResponseDto> getActiveAlerts() {
        return alertRepository.findAllActive().stream()
                .map(this::toAlertDto)
                .toList();
    }

    private ReportResponseDto toDto(CitizenReport r) {
        return new ReportResponseDto(
                r.getId(),
                r.getReportType(),
                r.getDescription(),
                r.getLatitude(),
                r.getLongitude(),
                r.getCreatedAt(),
                r.getStatus(),
                r.getClusterId()
        );
    }

    private ValidatedAlertResponseDto toAlertDto(ValidatedAlert a) {
        return new ValidatedAlertResponseDto(
                a.getId(),
                a.getAlertType(),
                a.getCentroidLat(),
                a.getCentroidLon(),
                a.getRadiusMeters(),
                a.getReportCount(),
                a.getConfirmedAt(),
                a.getStatus()
        );
    }
}
