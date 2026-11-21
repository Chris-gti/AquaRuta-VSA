package com.aquaruta.domain.repository;

import com.aquaruta.domain.model.WeatherCell;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para las cuadrículas meteorológicas.
 *
 * @author AquaRuta VSA Engineering Team
 */
public interface WeatherCellRepositoryPort {

    WeatherCell save(WeatherCell cell);

    List<WeatherCell> saveAll(List<WeatherCell> cells);

    Optional<WeatherCell> findById(String id);

    List<WeatherCell> findAll();

    /**
     * Determina la célula meteorológica más representativa o que contiene la coordenada indicada.
     *
     * @param latitude  Latitud
     * @param longitude Longitud
     * @return Célula meteorológica correspondiente
     */
    Optional<WeatherCell> findCellForCoordinate(double latitude, double longitude);
}
