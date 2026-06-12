package hr.algebra.surfspot.context;

import hr.algebra.surfspot.repository.sql.DataSourceFactory;
import javafx.stage.Stage;

import javax.sql.DataSource;

public class ApplicationContext {
    private final UserSession session;
    private final SceneNavigator navigator;
    private Stage primaryStage;

    private final RepositoryFactory repositories;
    private final ServiceFactory services;
    private final ControllerFactory controllers;

    public ApplicationContext() {
        this.session = new UserSession();
        this.navigator = new SceneNavigator(this);

        DataSource dataSource = DataSourceFactory.createDataSource();
        this.repositories = new RepositoryFactory(dataSource);
        this.services = new ServiceFactory(repositories);
        this.controllers = new ControllerFactory(services, session, navigator);
    }

    public Object getController(Class<?> controllerClass) {
        return controllers.getController(controllerClass);
    }

    public RepositoryFactory getRepositories() { return repositories; }
    public ServiceFactory getServices() { return services; }

    public UserSession getSession() { return session; }
    public SceneNavigator getSceneNavigator() { return navigator; }

    public boolean isAuthenticated() {
        return session.isAuthenticated();
    }

    public void setPrimaryStage(Stage stage) { this.primaryStage = stage; }
    public Stage getPrimaryStage() { return primaryStage; }
}