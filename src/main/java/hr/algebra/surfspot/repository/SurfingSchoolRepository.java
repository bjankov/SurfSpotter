package hr.algebra.surfspot.repository;

import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.model.SurfingSchool;

import java.util.List;

public interface SurfingSchoolRepository extends CrudRepository<SurfingSchool, Long> {
    List<SurfSpot> findSurfSpotsForSchool(Long schoolId);
}
