package com.aquaruta.infrastructure.persistence.repository;

import com.aquaruta.infrastructure.persistence.entity.WeatherCellEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para células meteorológicas con polígonos espaciales.
 */
@Repository
public interface SpringDataWeatherCellRepository extends JpaRepository<WeatherCellEntity, String> {

    @Query(value = """
        SELECT * FROM weather_cells c 
        WHERE ST_Contains(c.geom, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) 
        LIMIT 1
        """, nativeQuery = true)
    Optional<WeatherCellEntity> findContainingCellNative(@Param("lat") double lat, @Param("lon") double lon);
}
