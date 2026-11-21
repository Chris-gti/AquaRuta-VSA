package com.aquaruta.infrastructure.external.weather;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente HTTP para la API meteorológica abierta Open-Meteo.
 */
@Component
public class OpenMeteoClient {

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public OpenMeteoClient(RestTemplate restTemplate,
                           @Value("${aquaruta.apis.open-meteo-url:https://api.open-meteo.com/v1/forecast}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Consulta la precipitación actual en mm/h para una coordenada.
     */
    public double fetchCurrentPrecipitationMmH(double latitude, double longitude) {
        try {
            String url = String.format("%s?latitude=%.4f&longitude=%.4f&current=precipitation",
                    baseUrl, latitude, longitude);
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);

            if (response != null && response.has("current") && response.get("current").has("precipitation")) {
                return response.get("current").get("precipitation").asDouble(0.0);
            }
        } catch (Exception e) {
            log.warn("Fallo al consultar Open-Meteo para ({}, {}): {}. Usando fallback 0.0 mm/h",
                    latitude, longitude, e.getMessage());
        }
        return 0.0;
    }
}
