package com.aquaruta.interfaces.web;

import com.aquaruta.application.dto.ReportCreateDto;
import com.aquaruta.application.dto.ReportResponseDto;
import com.aquaruta.application.dto.ValidatedAlertResponseDto;
import com.aquaruta.application.service.WaterOutageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para reportes ciudadanos de agua potable y anegamiento.
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reportes Ciudadanos", description = "Crowdsourcing y alertas validadas por consenso espacio-temporal")
public class ReportController {

    private final WaterOutageService outageService;

    public ReportController(WaterOutageService outageService) {
        this.outageService = outageService;
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo reporte ciudadano",
               description = "Guarda el reporte y evalúa automáticamente el consenso por radio Haversine < 200m.")
    public ResponseEntity<ReportResponseDto> createReport(@Valid @RequestBody ReportCreateDto dto) {
        ReportResponseDto response = outageService.createReport(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos los reportes ciudadanos registrados")
    public ResponseEntity<List<ReportResponseDto>> getAllReports() {
        return ResponseEntity.ok(outageService.getAllReports());
    }

    @GetMapping("/alerts")
    @Operation(summary = "Listar zonas con alertas confirmadas por quórum")
    public ResponseEntity<List<ValidatedAlertResponseDto>> getActiveAlerts() {
        return ResponseEntity.ok(outageService.getActiveAlerts());
    }
}
