package hr.algebra.surfspot.repository.sql.mapper;

import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.model.Country;
import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.sql.RowMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating and managing RowMapper instances.
 * Provides type-safe access to mappers for different entity types.
 * Uses singleton pattern to ensure mapper reuse.
 */
public class RowMapperFactory {

    private static final RowMapperFactory INSTANCE = new RowMapperFactory();

    private final Map<Class<?>, RowMapper<?>> mappers = new HashMap<>();

    private RowMapperFactory() {
        // Register all available mappers
        mappers.put(User.class, new UserRowMapper());
        mappers.put(Country.class, new CountryRowMapper());
        mappers.put(Coast.class, new CoastRowMapper());
        // Add more mappers as needed
    }

    public static RowMapperFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Get a mapper for the specified entity type.
     * @param entityClass The entity class to get a mapper for
     * @return The mapper instance
     * @throws IllegalArgumentException if no mapper is registered for the type
     */
    @SuppressWarnings("unchecked")
    public <T> RowMapper<T> getMapper(Class<T> entityClass) {
        RowMapper<T> mapper = (RowMapper<T>) mappers.get(entityClass);
        if (mapper == null) {
            throw new IllegalArgumentException("No mapper registered for type: " + entityClass.getName());
        }
        return mapper;
    }

    /**
     * Register a custom mapper for a specific entity type.
     * Useful for testing or custom mapping scenarios.
     */
    public <T> void registerMapper(Class<T> entityClass, RowMapper<T> mapper) {
        mappers.put(entityClass, mapper);
    }
}
