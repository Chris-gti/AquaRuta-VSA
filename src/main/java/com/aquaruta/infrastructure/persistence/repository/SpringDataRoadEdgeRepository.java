package com.aquaruta.infrastructure.persistence.repository;

import com.aquaruta.infrastructure.persistence.entity.RoadEdgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para tramos y aristas viales.
 */
@Repository
public interface SpringDataRoadEdgeRepository extends JpaRepository<RoadEdgeEntity, String> {

    List<RoadEdgeEntity> findBySourceNodeId(Long sourceNodeId);
}
