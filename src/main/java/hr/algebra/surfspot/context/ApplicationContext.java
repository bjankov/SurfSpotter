package hr.algebra.surfspot.context;

import hr.algebra.surfspot.model.User;
import hr.algebra.surfspot.repository.UserRepository;
import hr.algebra.surfspot.repository.sql.DataSourceSingleton;
import hr.algebra.surfspot.security.BCryptPasswordService;
import hr.algebra.surfspot.security.PasswordService;
import hr.algebra.surfspot.service.AuthService;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Application-wide context singleton that manages:
 * - Current authenticated user session
 * - Dependency injection (repositories, services)
 * - Scene navigation
 * - Primary stage reference
 */
public class ApplicationContext {
    private static final Logger log = LoggerFactory.getLogger(ApplicationContext.class);

    private static class InstanceHolder {
        private static final ApplicationContext INSTANCE = new ApplicationContext();
    }

    public static ApplicationContext getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private final RepositoryRegistry repositoryRegistry;
    private final PasswordService passwordService;
    private final AuthService authService;
    private final SceneNavigator sceneNavigator;

    private User currentUser;
    private Stage primaryStage;

    private ApplicationContext() {
        log.info("Initializing ApplicationContext");

        DataSource dataSource = DataSourceSingleton.getInstance();
        this.repositoryRegistry = new RepositoryRegistry(dataSource);

        this.passwordService = new BCryptPasswordService();
        UserRepository userRepository = repositoryRegistry.getRepository(UserRepository.class);
        this.authService = new AuthService(userRepository, passwordService);

        this.sceneNavigator = new SceneNavigator(this);

        log.info("ApplicationContext initialized successfully");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        log.info("Current user set to: {}", user != null ? user.getUsername() : "null");
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public void logout() {
        log.info("User {} logging out", currentUser != null ? currentUser.getUsername() : "unknown");
        this.currentUser = null;
        sceneNavigator.navigateToLogin();
    }

    public RepositoryRegistry getRepositoryRegistry() {
        return repositoryRegistry;
    }

    public PasswordService getPasswordService() {
        return passwordService;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public SceneNavigator getSceneNavigator() {
        return sceneNavigator;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
