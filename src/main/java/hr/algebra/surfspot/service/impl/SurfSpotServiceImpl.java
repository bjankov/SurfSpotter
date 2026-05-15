package hr.algebra.surfspot.service.impl;

import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.repository.SurfSpotRepository;
import hr.algebra.surfspot.service.SurfSpotService;

import java.util.List;
import java.util.Optional;

public class SurfSpotServiceImpl implements SurfSpotService {
    private final SurfSpotRepository surfSpotRepository;

    public SurfSpotServiceImpl(SurfSpotRepository surfSpotRepository) {
        this.surfSpotRepository = surfSpotRepository;
    }

    @Override
    public List<SurfSpot> findAll() {
        return surfSpotRepository.findAll();
    }

    @Override
    public Optional<SurfSpot> findById(Long id) {
        return surfSpotRepository.findById(id);
    }

    @Override
    public SurfSpot save(SurfSpot spot) {
        return surfSpotRepository.save(spot);
    }

    @Override
    public void update(SurfSpot spot) {
        surfSpotRepository.update(spot);
    }

    @Override
    public void delete(Long id) {
        surfSpotRepository.delete(id);
    }
}
