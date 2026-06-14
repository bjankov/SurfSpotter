package hr.algebra.surfspot.service;

import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.model.SurfingSchool;

import java.util.List;

public interface SurfingSchoolService extends BaseService<SurfingSchool, Long> {
    List<SurfSpot> findSurfSpotsForSchool(Long schoolId);
    void updateSurfSpots(Long schoolId, List<Long> surfSpotIds);
}
