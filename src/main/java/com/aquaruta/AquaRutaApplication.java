package com.aquaruta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de inicio para AquaRuta VSA.
 * <p>
 * Plataforma geoespacial resiliente de enrutamiento vial anti-inundaciones
 * y monitoreo colaborativo de cortes de agua potable en Villahermosa, Tabasco.
 * </p>
 *
 * @author AquaRuta VSA Engineering Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class AquaRutaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AquaRutaApplication.class, args);
    }
}
