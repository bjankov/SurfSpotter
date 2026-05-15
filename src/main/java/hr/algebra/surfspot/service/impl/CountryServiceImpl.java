package hr.algebra.surfspot.service.impl;

import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.repository.CountryRepository;
import hr.algebra.surfspot.service.CountryService;

import java.util.List;
import java.util.Optional;

public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    @Override
    public Optional<Country> findById(String code) {
        return countryRepository.findById(code);
    }

    @Override
    public Country save(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public void update(Country country) {
        countryRepository.save(country);
    }

    @Override
    public void delete(String code) {
        countryRepository.delete(code);
    }
}
