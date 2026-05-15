package hr.algebra.surfspot.service.impl;

import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.repository.SurfingSchoolRepository;
import hr.algebra.surfspot.service.SurfingSchoolService;

import java.util.List;
import java.util.Optional;

public class SurfingSchoolServiceImpl implements SurfingSchoolService {
    private final SurfingSchoolRepository surfingSchoolRepository;

    public SurfingSchoolServiceImpl(SurfingSchoolRepository surfingSchoolRepository) {
        this.surfingSchoolRepository = surfingSchoolRepository;
    }

    @Override
    public List<SurfingSchool> findAll() {
        return surfingSchoolRepository.findAll();
    }

    @Override
    public Optional<SurfingSchool> findById(Long id) {
        return surfingSchoolRepository.findById(id);
    }

    @Override
    public SurfingSchool save(SurfingSchool school) {
        return surfingSchoolRepository.save(school);
    }

    @Override
    public void update(SurfingSchool school) {
        surfingSchoolRepository.update(school);
    }

    @Override
    public void delete(Long id) {
        surfingSchoolRepository.delete(id);
    }
}
