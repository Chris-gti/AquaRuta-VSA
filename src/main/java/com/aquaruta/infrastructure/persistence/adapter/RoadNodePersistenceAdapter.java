package com.aquaruta.infrastructure.persistence.adapter;

import com.aquaruta.domain.model.RoadNode;
import com.aquaruta.domain.repository.RoadNodeRepositoryPort;
import com.aquaruta.infrastructure.config.SpatialConfig;
import com.aquaruta.infrastructure.persistence.entity.RoadNodeEntity;
import com.aquaruta.infrastructure.persistence.repository.SpringDataRoadNodeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para nodos viales en PostgreSQL/PostGIS.
 */
@Component
public class RoadNodePersistenceAdapter implements RoadNodeRepositoryPort {

    private final SpringDataRoadNodeRepository nodeRepository;

    public RoadNodePersistenceAdapter(SpringDataRoadNodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    public RoadNode save(RoadNode node) {
        RoadNodeEntity entity = toEntity(node);
        RoadNodeEntity saved = nodeRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<RoadNode> saveAll(List<RoadNode> nodes) {
        List<RoadNodeEntity> entities = nodes.stream().map(this::toEntity).toList();
        return nodeRepository.saveAll(entities).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<RoadNode> findById(Long id) {
        return nodeRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<RoadNode> findAll() {
        return nodeRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public long count() {
        return nodeRepository.count();
    }

    @Override
    public Optional<RoadNode> findNearestNode(double latitude, double longitude) {
        return nodeRepository.findNearestNodeNative(latitude, longitude).map(this::toDomain);
    }

    private RoadNodeEntity toEntity(RoadNode d) {
        return new RoadNodeEntity(
                d.getId(),
                d.getLatitude(),
                d.getLongitude(),
                d.getElevationMsnm(),
                SpatialConfig.createPoint(d.getLatitude(), d.getLongitude())
        );
    }

    private RoadNode toDomain(RoadNodeEntity e) {
        return new RoadNode(e.getId(), e.getLatitude(), e.getLongitude(), e.getElevationMsnm());
    }
}
