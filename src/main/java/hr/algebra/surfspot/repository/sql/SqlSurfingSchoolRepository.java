package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.model.SurfingSchool;
import hr.algebra.surfspot.repository.SurfingSchoolRepository;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class SqlSurfingSchoolRepository extends BaseSqlRepository<SurfingSchool> implements SurfingSchoolRepository {
    private final RowMapper<SurfingSchool> surfingSchoolMapper;
    private final RowMapper<SurfSpot> surfSpotMapper;

    public SqlSurfingSchoolRepository(
            DataSource dataSource,
            RowMapper<SurfingSchool> surfingSchoolMapper,
            RowMapper<SurfSpot> surfSpotMapper) {
        super(dataSource);
        this.surfingSchoolMapper = surfingSchoolMapper;
        this.surfSpotMapper = surfSpotMapper;
    }

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM surfing_schools WHERE id = ?";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM surfing_schools WHERE name = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM surfing_schools";
    private static final String SAVE_QUERY = "INSERT INTO surfing_schools (name) VALUES (?)";
    private static final String UPDATE_BY_ID = "UPDATE surfing_schools SET name = ? WHERE id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM surfing_schools WHERE id = ?";
    private static final String FIND_SPOTS_FOR_SCHOOL_ID_QUERY = """
    SELECT
        ss.id AS id,
        ss.name AS surf_spot_name,
        cnt.code AS country_code,
        cnt.name AS country_name,
        cst.id AS coast_id,
        cst.name AS coast_name,
        wind_direction,
        wave_type,
        wave_height,
        latitude,
        longitude,
        difficulty,
        image_path
    FROM surfing_schools sch
        JOIN surf_spot_schools sss ON sss.school_id = sch.id
        JOIN surf_spots ss ON ss.id = sss.surf_spot_id
        JOIN coasts cst ON cst.id = ss.coast_id
        JOIN countries cnt ON cnt.code = cst.country_code
    WHERE school_id = ?
    """;

    @Override
    public Optional<SurfingSchool> findById(Long id) {
        return findSingleResult(FIND_BY_ID_QUERY, surfingSchoolMapper, id);
    }

    public Optional<SurfingSchool> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, surfingSchoolMapper, name);
    }

    @Override
    public List<SurfingSchool> findAll() {
        return findAll(FIND_ALL_QUERY, surfingSchoolMapper);
    }

    public SurfingSchool save(SurfingSchool surfingSchool) {
        if (surfingSchool.getId() != null) {
            return update(surfingSchool);
        }
        Long generatedId = insertAndGetId(SAVE_QUERY, surfingSchool.getName());

        requireGeneratedId(generatedId, "Could not save surfing school");

        return SurfingSchool.builder()
                .from(surfingSchool)
                .id(generatedId)
                .build();
    }

    @Override
    public SurfingSchool update(SurfingSchool school) {
        int affectedRows = executeUpdate(
                UPDATE_BY_ID,
                school.getName(),
                school.getId()
        );
        requireAffectedRows(affectedRows, "Could not update surfing school with ID: " + school.getId());
        return school;
    }

    @Override
    public void delete(Long id) {
        int affectedRows = executeUpdate(DELETE_BY_ID_QUERY, id);
        requireAffectedRows(affectedRows, "Could not delete surfing school with ID: " + id);
    }

    @Override
    public List<SurfSpot> findSurfSpotsForSchool(Long schoolId) {
        return findAll(FIND_SPOTS_FOR_SCHOOL_ID_QUERY, surfSpotMapper, schoolId);
    }
}
