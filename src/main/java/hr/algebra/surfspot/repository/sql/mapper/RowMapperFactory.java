package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.*;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.util.HashMap;
import java.util.Map;

public class RowMapperFactory {

    private static final RowMapperFactory INSTANCE = new RowMapperFactory();

    private final Map<Class<?>, RowMapper<?>> mappers = new HashMap<>();

    private RowMapperFactory() {
        mappers.put(Coast.class, new CoastRowMapper());
        mappers.put(Country.class, new CountryRowMapper());
        mappers.put(Instructor.class, new InstructorRowMapper());
        mappers.put(Role.class, new RoleRowMapper());
        mappers.put(SurfingSchool.class, new SurfingSchoolRowMapper());
        mappers.put(SurfSpot.class, new SurfSpotRowMapper());
        mappers.put(User.class, new UserRowMapper());
    }

    public static RowMapperFactory getInstance() {
        return INSTANCE;
    }

    public <T> RowMapper<T> getMapper(Class<T> entityClass) {
        RowMapper<T> mapper = (RowMapper<T>) mappers.get(entityClass);
        if (mapper == null) {
            throw new IllegalArgumentException("No mapper registered for type: " + entityClass.getName());
        }
        return mapper;
    }

    public <T> void registerMapper(Class<T> entityClass, RowMapper<T> mapper) {
        mappers.put(entityClass, mapper);
    }
}
