package com.aquaruta.infrastructure.persistence.adapter;

import com.aquaruta.domain.model.RoadEdge;
import com.aquaruta.domain.repository.RoadEdgeRepositoryPort;
import com.aquaruta.infrastructure.config.SpatialConfig;
import com.aquaruta.infrastructure.persistence.entity.RoadEdgeEntity;
import com.aquaruta.infrastructure.persistence.repository.SpringDataRoadEdgeRepository;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para tramos viales en PostgreSQL/PostGIS.
 */
@Component
public class RoadEdgePersistenceAdapter implements RoadEdgeRepositoryPort {

    private final SpringDataRoadEdgeRepository edgeRepository;

    public RoadEdgePersistenceAdapter(SpringDataRoadEdgeRepository edgeRepository) {
        this.edgeRepository = edgeRepository;
    }

    @Override
    public RoadEdge save(RoadEdge edge) {
        RoadEdgeEntity entity = toEntity(edge);
        return toDomain(edgeRepository.save(entity));
    }

    @Override
    public List<RoadEdge> saveAll(List<RoadEdge> edges) {
        List<RoadEdgeEntity> entities = edges.stream().map(this::toEntity).toList();
        return edgeRepository.saveAll(entities).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<RoadEdge> findById(String id) {
        return edgeRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<RoadEdge> findAll() {
        return edgeRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<RoadEdge> findBySourceNodeId(Long sourceNodeId) {
        return edgeRepository.findBySourceNodeId(sourceNodeId).stream().map(this::toDomain).toList();
    }

    @Override
    public long count() {
        return edgeRepository.count();
    }

    private RoadEdgeEntity toEntity(RoadEdge d) {
        List<Coordinate> coords = List.of(new Coordinate(0.0, 0.0), new Coordinate(0.0, 0.0));
        return new RoadEdgeEntity(
                d.getId(),
                d.getSourceNodeId(),
                d.getTargetNodeId(),
                d.getStreetName(),
                d.getHighwayType(),
                d.getLengthMeters(),
                d.isOneway(),
                d.getMaxspeed(),
                d.getBaseRiskFactor(),
                SpatialConfig.createLineString(coords)
        );
    }

    private RoadEdge toDomain(RoadEdgeEntity e) {
        return new RoadEdge(
                e.getId(),
                e.getSourceNodeId(),
                e.getTargetNodeId(),
                e.getStreetName(),
                e.getHighwayType(),
                e.getLengthMeters(),
                e.getOneway() != null ? e.getOneway() : false,
                e.getMaxspeed() != null ? e.getMaxspeed() : 40,
                e.getBaseRiskFactor() != null ? e.getBaseRiskFactor() : 1.0
        );
    }
}
