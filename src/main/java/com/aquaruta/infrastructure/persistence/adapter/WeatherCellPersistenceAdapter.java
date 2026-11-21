package com.aquaruta.infrastructure.persistence.adapter;

import com.aquaruta.domain.model.WeatherCell;
import com.aquaruta.domain.repository.WeatherCellRepositoryPort;
import com.aquaruta.infrastructure.persistence.entity.WeatherCellEntity;
import com.aquaruta.infrastructure.persistence.repository.SpringDataWeatherCellRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia para células meteorológicas en PostgreSQL/PostGIS.
 */
@Component
public class WeatherCellPersistenceAdapter implements WeatherCellRepositoryPort {

    private final SpringDataWeatherCellRepository cellRepository;

    public WeatherCellPersistenceAdapter(SpringDataWeatherCellRepository cellRepository) {
        this.cellRepository = cellRepository;
    }

    @Override
    public WeatherCell save(WeatherCell cell) {
        WeatherCellEntity entity = cellRepository.findById(cell.getId()).orElse(null);
        if (entity != null) {
            entity.setPrecipitationMmH(cell.getPrecipitationMmH());
            entity.setLastUpdated(cell.getLastUpdated());
            return toDomain(cellRepository.save(entity));
        }
        return cell;
    }

    @Override
    public List<WeatherCell> saveAll(List<WeatherCell> cells) {
        return cells.stream().map(this::save).toList();
    }

    @Override
    public Optional<WeatherCell> findById(String id) {
        return cellRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<WeatherCell> findAll() {
        return cellRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<WeatherCell> findCellForCoordinate(double latitude, double longitude) {
        return cellRepository.findContainingCellNative(latitude, longitude).map(this::toDomain);
    }

    private WeatherCell toDomain(WeatherCellEntity e) {
        return new WeatherCell(
                e.getId(),
                e.getName(),
                e.getCenterLat(),
                e.getCenterLon(),
                e.getPrecipitationMmH(),
                e.getLastUpdated()
        );
    }
}
