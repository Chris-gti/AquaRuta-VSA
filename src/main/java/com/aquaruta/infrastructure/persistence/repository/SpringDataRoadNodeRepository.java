package com.aquaruta.infrastructure.persistence.repository;

import com.aquaruta.infrastructure.persistence.entity.RoadNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para nodos viales con consultas espaciales PostGIS.
 */
@Repository
public interface SpringDataRoadNodeRepository extends JpaRepository<RoadNodeEntity, Long> {

    @Query(value = "SELECT * FROM road_nodes n ORDER BY ST_Distance(n.geom, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) ASC LIMIT 1", nativeQuery = true)
    Optional<RoadNodeEntity> findNearestNodeNative(@Param("lat") double lat, @Param("lon") double lon);
}
