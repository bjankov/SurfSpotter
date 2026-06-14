package hr.algebra.surfspot.service.impl;

import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.repository.CoastRepository;
import hr.algebra.surfspot.service.CoastService;

import java.util.List;
import java.util.Optional;

public class CoastServiceImpl implements CoastService {
    private final CoastRepository coastRepository;

    public CoastServiceImpl(final CoastRepository coastRepository) {
        this.coastRepository = coastRepository;
    }

    @Override
    public List<Coast> findAll() {
        return coastRepository.findAll();
    }

    @Override
    public Optional<Coast> findById(Long id) {
        return coastRepository.findById(id);
    }

    @Override
    public Coast save(Coast coast) {
        return coastRepository.save(coast);
    }

    @Override
    public Coast update(Coast coast) {
        return coastRepository.update(coast);
    }

    @Override
    public void delete(Long id) {
        coastRepository.delete(id);
    }
}
