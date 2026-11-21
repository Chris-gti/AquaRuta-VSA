package com.aquaruta.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI 3.0 para la documentación interactiva Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AquaRuta VSA API - Enrutamiento Resiliente y Monitoreo Hídrico")
                        .version("1.0.0")
                        .description("""
                            API REST de AquaRuta VSA para el polígono céntrico de Villahermosa, Tabasco.
                            
                            Resuelve la crisis del agua y anegamientos urbanos mediante:
                            - Enrutamiento vial resiliente usando algoritmo A* con heurística geodésica y ponderación por altimetría.
                            - Consenso espacio-temporal de reportes ciudadanos de falta de agua y calles inundadas (Haversine < 200m).
                            - Modo Simulación interactivo para inyección de escenarios de lluvia y ráfagas de reportes en vivo.
                            """)
                        .contact(new Contact()
                                .name("Equipo de Ingeniería AquaRuta VSA")
                                .email("contacto@aquaruta-vsa.edu.mx"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
