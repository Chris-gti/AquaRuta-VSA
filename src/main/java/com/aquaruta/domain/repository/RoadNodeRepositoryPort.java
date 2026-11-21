package com.aquaruta.domain.repository;

import com.aquaruta.domain.model.RoadNode;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio para la gestión de nodos viales.
 *
 * @author AquaRuta VSA Engineering Team
 */
public interface RoadNodeRepositoryPort {

    RoadNode save(RoadNode node);

    List<RoadNode> saveAll(List<RoadNode> nodes);

    Optional<RoadNode> findById(Long id);

    List<RoadNode> findAll();

    long count();

    /**
     * Localiza el nodo vial más cercano a una coordenada geográfica dada (Spatial Snapping).
     *
     * @param latitude  Latitud origen
     * @param longitude Longitud origen
     * @return Nodo más cercano en la red vial
     */
    Optional<RoadNode> findNearestNode(double latitude, double longitude);
}
