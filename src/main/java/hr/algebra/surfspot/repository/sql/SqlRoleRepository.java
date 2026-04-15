package hr.algebra.surfspot.repository.sql;

import hr.algebra.surfspot.exception.RepositoryException;
import hr.algebra.surfspot.model.Permission;
import hr.algebra.surfspot.model.Role;
import hr.algebra.surfspot.repository.RoleRepository;
import hr.algebra.surfspot.repository.sql.mapper.RowMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SqlRoleRepository extends BaseSqlRepository<Role> implements RoleRepository {
    private static final Logger log =  LoggerFactory.getLogger(SqlRoleRepository.class);
    private final RowMapper<Role> roleMapper;

    public SqlRoleRepository(DataSource dataSource) {
        super(dataSource);
        roleMapper = RowMapperFactory.getInstance().getMapper(Role.class);
    }

    public static final String FIND_BY_ID_QUERY = "SELECT * FROM roles WHERE id = ?";
    public static final String FIND_BY_NAME_QUERY = "SELECT * FROM roles WHERE id = ?";
    public static final String FIND_ALL_QUERY = "SELECT * FROM roles";
    public static final String SAVE_QUERY = "INSERT INTO roles (name) VALUES (?)";
    public static final String DELETE_BY_ID_QUERY = "DELETE FROM roles WHERE id = ?";

    @Override
    public Optional<Role> findById(String id) {
        return findSingleResult(FIND_BY_ID_QUERY, roleMapper, id);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return findSingleResult(FIND_BY_NAME_QUERY, roleMapper, name);
    }

    @Override
    public List<Role> findAll() {
        return findAll(FIND_ALL_QUERY, roleMapper);
    }

    @Override
    public Role save(Role role) {
        Long generatedId = insertAndGetId(SAVE_QUERY);

        if (generatedId != null) {
            return Role.builder()
                    .from(role)
                    .id(generatedId)
                    .build();
        }
        throw  new RepositoryException("Role could not be saved");
    }

    @Override
    public void delete(String id) {
       executeUpdate(DELETE_BY_ID_QUERY, id);
    }

}
