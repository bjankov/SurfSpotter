package hr.algebra.surfspot.context;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.repository.*;
import hr.algebra.surfspot.repository.sql.*;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class RepositoryRegistry {
    private final Map<Class<?>, Object> registry = new HashMap<>();

    public RepositoryRegistry(DataSource dataSource) {
        registry.put(SurfSpotRepository.class, new SqlSurfSpotRepository(dataSource));
        registry.put(UserRepository.class, new SqlUserRepository(dataSource));
        registry.put(CoastRepository.class, new SqlCoastRepository(dataSource));
        registry.put(CountryRepository.class, new SqlCountryRepository(dataSource));
        registry.put(InstructorRepository.class, new SqlInstructorRepository(dataSource));
        registry.put(PermissionRepository.class, new SqlPermissionRepository(dataSource));
        registry.put(RoleRepository.class, new SqlRoleRepository(dataSource));
        registry.put(SurfingSchoolRepository.class, new SqlSurfingSchoolRepository(dataSource));
    }

    public <T> T getRepository(Class<T> repositoryInterface) {
        T repository = (T) registry.get(repositoryInterface);
        if (repository == null) {
            throw new RepositoryException("No repository found for interface " + repositoryInterface.getName());
        }
        return repository;
    }
}
