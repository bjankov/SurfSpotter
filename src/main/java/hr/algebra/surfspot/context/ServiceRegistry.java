package hr.algebra.surfspot.context;

import hr.algebra.surfspot.repository.UserRepository;
import hr.algebra.surfspot.repository.sql.DataSourceFactory;
import hr.algebra.surfspot.security.BCryptPasswordService;
import hr.algebra.surfspot.security.PasswordService;
import hr.algebra.surfspot.service.AuthService;
import hr.algebra.surfspot.validation.UserValidator;

import javax.sql.DataSource;

public class ServiceRegistry {
    private final RepositoryRegistry repositoryRegistry;
    private final AuthService authService;
    private final PasswordService passwordService;

    public ServiceRegistry() {
        DataSource dataSource = DataSourceFactory.createDataSource();

        this.repositoryRegistry = new RepositoryRegistry(dataSource);
        UserRepository userRepository = repositoryRegistry.getRepository(UserRepository.class);

        this.passwordService = new BCryptPasswordService();
        UserValidator userValidator = new UserValidator(userRepository);

        this.authService = new AuthService(userRepository, passwordService, userValidator);
    }

    public AuthService getAuthService() {
        return authService;
    }
}