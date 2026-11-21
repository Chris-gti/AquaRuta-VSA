package com.aquaruta.domain.repository;

import com.aquaruta.domain.model.RoadEdge;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la gestión de aristas o tramos viales.
 *
 * @author AquaRuta VSA Engineering Team
 */
public interface RoadEdgeRepositoryPort {

    RoadEdge save(RoadEdge edge);

    List<RoadEdge> saveAll(List<RoadEdge> edges);

    Optional<RoadEdge> findById(String id);

    List<RoadEdge> findAll();

    List<RoadEdge> findBySourceNodeId(Long sourceNodeId);

    long count();
}
