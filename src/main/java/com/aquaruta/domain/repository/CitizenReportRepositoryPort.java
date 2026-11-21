package com.aquaruta.domain.repository;

import com.aquaruta.domain.model.CitizenReport;
import com.aquaruta.domain.model.ReportStatus;
import com.aquaruta.domain.model.ReportType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para el almacenamiento y consulta de reportes ciudadanos.
 *
 * @author AquaRuta VSA Engineering Team
 */
public interface CitizenReportRepositoryPort {

    CitizenReport save(CitizenReport report);

    Optional<CitizenReport> findById(String id);

    List<CitizenReport> findAll();

    List<CitizenReport> findRecentActiveReports(ReportType type, Instant since);

    /**
     * Busca reportes del mismo tipo dentro de un radio de distancia en metros a partir de un punto central.
     *
     * @param latitude     Latitud central
     * @param longitude    Longitud central
     * @param radiusMeters Radio de búsqueda en metros
     * @param type         Tipo de reporte
     * @param since        Umbral temporal mínimo
     * @return Lista de reportes encontrados
     */
    List<CitizenReport> findNearbyReports(double latitude,
                                         double longitude,
                                         double radiusMeters,
                                         ReportType type,
                                         Instant since);

    void updateStatus(String reportId, ReportStatus status, String clusterId);
}
