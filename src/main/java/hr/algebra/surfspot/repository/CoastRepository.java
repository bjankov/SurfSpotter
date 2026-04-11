package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.Coast;

import java.util.Optional;

public interface CoastRepository extends CrudRepository<Coast, Long> {
    Optional<Coast> findfindByCountryCode(String countryCode);
}
