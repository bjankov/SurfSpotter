package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.PersistenceException;
import hr.algebra.surfspot.model.*;
import hr.algebra.surfspot.repository.SurfSpotRepository;
import javax.sql.DataSource;
import java.util.*;

public class SqlSurfSpotRepository extends BaseSqlRepository<SurfSpot> implements SurfSpotRepository {
    private final RowMapper<SurfSpot> surfSpotMapper;
    private final RowMapper<Instructor> instructorMapper;

    public SqlSurfSpotRepository(DataSource dataSource,  RowMapper<SurfSpot> surfSpotMapper, RowMapper<Instructor> instructorMapper) {
        super(dataSource);
        this.surfSpotMapper = surfSpotMapper;
        this.instructorMapper = instructorMapper;
    }

    private static final String UPDATE_BY_ID_QUERY = """
            UPDATE surf_spots
            SET name = ?, latitude = ?, longitude = ?, coast_id = ?,
                wave_type = ?, wave_height = ?, difficulty = ?,
                wind_direction = ?, image_path = ?
            WHERE id = ?;""";

    private static final String SAVE_SURF_SPOT_QUERY = """
            INSERT INTO surf_spots (
                name, latitude, longitude, coast_id, wave_type,
                wave_height, difficulty, wind_direction, image_path
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);""";

    private static final String DELETE_BY_ID_QUERY = "DELETE FROM surf_spots WHERE id = ?";

    private static final String FIND_MONTHS_BY_SPOT_ID = "SELECT month_name FROM surf_spot_months WHERE surf_spot_id = ?";
    private static final String INSERT_MONTHS_QUERY = "INSERT INTO surf_spot_months (surf_spot_id, month_name) VALUES (?, ?)";
    private static final String DELETE_MONTHS_QUERY = "DELETE FROM surf_spot_months WHERE surf_spot_id = ?";

    private static final String FIND_INSTRUCTORS_BY_SPOT_ID = """
        SELECT
            i.id as instructor_id,
            i.first_name,
            i.last_name,
            i.surfing_school_id AS surfing_school_id,
            sch.name AS surfing_school_name
        FROM instructors i
            JOIN surfing_schools sch ON i.surfing_school_id = sch.id
            JOIN surf_spot_schools sss ON sss.school_id = sch.id
            JOIN surf_spots ss ON sss.surf_spot_id = ss.id
        WHERE ss.id = ?
        """;

    private static final String BASE_SELECT = """
            SELECT
                ss.id, ss.name AS surf_spot_name, ss.latitude, ss.longitude,
                ss.wave_type, ss.wave_height, ss.difficulty, ss.wind_direction,
                coasts.id AS coast_id, coasts.name AS coast_name,
                countries.name AS country_name, countries.code AS country_code,
                ss.image_path
            FROM surf_spots ss
            JOIN coasts ON ss.coast_id = coasts.id
            JOIN countries ON coasts.country_code = countries.code
            """;

    private static final String FIND_BY_ID_QUERY = BASE_SELECT + " WHERE ss.id = ?";
    private static final String FIND_ALL_QUERY = BASE_SELECT;

    @Override
    public Optional<SurfSpot> findById(Long id) {
        Optional<SurfSpot> spot = findSingleResult(FIND_BY_ID_QUERY, surfSpotMapper, id);
        spot.ifPresent(this::populateAssociations);
        return spot;
    }

    @Override
    public List<SurfSpot> findAll() {
        return fetchAndPopulate(FIND_ALL_QUERY);
    }

    @Override
    public SurfSpot save(SurfSpot spot) {
        if (spot.getId() != null) {
            return update(spot);
        }

        Long generatedId = insertAndGetId(
                SAVE_SURF_SPOT_QUERY,
                spot.getName(),
                spot.getLatitude(),
                spot.getLongitude(),
                spot.getCoastId(),
                spot.getWaveType() != null ? spot.getWaveType().name() : null,
                spot.getWaveHeight(),
                spot.getDifficulty().name(),
                spot.getWindDirectionDegrees(),
                spot.getImagePath()
        );

        if (generatedId != null) {
            saveMonths(generatedId, spot.getBestSeason());

            return SurfSpot.builder()
                    .id(generatedId)
                    .name(spot.getName())
                    .location(spot.getLocation())
                    .bestSeason(spot.getBestSeason())
                    .difficulty(spot.getDifficulty())
                    .windDirectionDegrees(spot.getWindDirectionDegrees())
                    .waveDetails(spot.getWaveDetails())
                    .imagePath(spot.getImagePath())
                    .build();
        }
        throw new PersistenceException("Could not save surf spot");
    }

    @Override
    public SurfSpot update(SurfSpot spot) {
        int affectedRows = executeUpdate(
                UPDATE_BY_ID_QUERY,
                spot.getName(),
                spot.getLatitude(),
                spot.getLongitude(),
                spot.getCoastId(),
                spot.getWaveType() != null ? spot.getWaveType().name() : null,
                spot.getWaveHeight(),
                spot.getDifficulty().name(),
                spot.getWindDirectionDegrees(),
                spot.getImagePath(),
                spot.getId()
        );

        requireAffectedRows(affectedRows, "Could not update surf spot with ID: " + spot.getId());

        executeUpdate(DELETE_MONTHS_QUERY, spot.getId());
        saveMonths(spot.getId(), spot.getBestSeason());

        return spot;
    }

    @Override
    public void delete(Long id) {
        int affectedRows = executeUpdate(
                DELETE_BY_ID_QUERY,
                id
        );
        requireAffectedRows(affectedRows, "Could not delete surf spot with ID: " + id);
    }

    private void populateAssociations(SurfSpot spot) {
        spot.setBestSeason(fetchMonthsForSpot(spot.getId()));
        spot.setInstructors (fetchInstructorsForSpot(spot.getId()));
    }

    private List<SurfSpot> fetchAndPopulate(String query, Object... params) {
        List<SurfSpot> spots = findAll(query, surfSpotMapper, params);
        spots.forEach(this::populateAssociations);
        return spots;
    }

    private EnumSet<Month> fetchMonthsForSpot(Long surfSpotId) {
        List<Month> months = findAll(
                FIND_MONTHS_BY_SPOT_ID,
                resultSet -> Month.valueOf(resultSet.getString("month_name")),
                surfSpotId);

        if (months.isEmpty()) {
            return EnumSet.noneOf(Month.class);
        }
        return EnumSet.copyOf(months);
    }

    private Set<Instructor> fetchInstructorsForSpot(Long surfSpotId) {
        List<Instructor> instructorList = findAll(FIND_INSTRUCTORS_BY_SPOT_ID, instructorMapper, surfSpotId);
        return new HashSet<>(instructorList);
    }

    private void saveMonths(Long surfSpotId, Set<Month> months) {
        if (months == null || months.isEmpty()) return;
        for (final Month month : months) {
            executeUpdate(INSERT_MONTHS_QUERY, surfSpotId, month.name());
        }
    }
}