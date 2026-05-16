package hr.algebra.surfspot.context;

import hr.algebra.surfspot.controller.MainLayoutController;
import hr.algebra.surfspot.controller.auth.AuthLayoutController;
import hr.algebra.surfspot.controller.auth.LoginController;
import hr.algebra.surfspot.controller.auth.RegisterController;
import hr.algebra.surfspot.controller.coast.CoastFormController;
import hr.algebra.surfspot.controller.coast.CoastListController;
import hr.algebra.surfspot.controller.instructor.InstructorFormController;
import hr.algebra.surfspot.controller.instructor.InstructorListController;
import hr.algebra.surfspot.controller.school.SurfingSchoolFormController;
import hr.algebra.surfspot.controller.school.SurfingSchoolListController;
import hr.algebra.surfspot.controller.surfspot.SurfSpotFormController;
import hr.algebra.surfspot.controller.surfspot.SurfSpotListController;
import hr.algebra.surfspot.repository.sql.DataSourceFactory;
import hr.algebra.surfspot.service.*;
import javafx.stage.Stage;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ApplicationContext {
    private static ApplicationContext instance;

    private final ServiceRegistry services;
    private final UserSession session;
    private final SceneNavigator navigator;
    private Stage primaryStage;
    private final Map<Class<?>, Supplier<?>> controllerFactories = new HashMap<>();

    private ApplicationContext() {
        DataSource dataSource = DataSourceFactory.createDataSource();
        RepositoryRegistry repositoryRegistry = new RepositoryRegistry(dataSource);

        this.services = new ServiceRegistry(repositoryRegistry);

        this.session = new UserSession();
        this.navigator = new SceneNavigator(this);

        initControllerFactories();
    }

    public static synchronized ApplicationContext getInstance() {
        if (instance == null) instance = new ApplicationContext();
        return instance;
    }

    private void initControllerFactories() {
        controllerFactories.put(LoginController.class, () ->
                new LoginController(getAuthService(), getSession(), getSceneNavigator()));

        controllerFactories.put(RegisterController.class, () ->
                new RegisterController(getAuthService(), getSession(), getSceneNavigator()));

        controllerFactories.put(AuthLayoutController.class, () ->
                new AuthLayoutController(getSceneNavigator()));

        controllerFactories.put(MainLayoutController.class, () ->
                new MainLayoutController(getSceneNavigator()));

        controllerFactories.put(InstructorListController.class, () ->
                new InstructorListController(getInstructorService(),  getSceneNavigator()));

        controllerFactories.put(InstructorFormController.class, () ->
                new  InstructorFormController(getInstructorService(), getSceneNavigator()));

        controllerFactories.put(SurfSpotListController.class, () ->
                new SurfSpotListController(getSurfSpotService(), getSceneNavigator()));

         controllerFactories.put(SurfSpotFormController.class, () ->
                 new SurfSpotFormController(getSurfSpotService(), getCoastService(), getSceneNavigator()));

         controllerFactories.put(SurfingSchoolListController.class, () ->
                 new SurfingSchoolListController(getSurfingSchoolService(), getSceneNavigator()));

         controllerFactories.put(SurfingSchoolFormController.class, () ->
                 new SurfingSchoolFormController(getSurfingSchoolService(), getSceneNavigator()));

         controllerFactories.put(CoastListController.class, () ->
                 new CoastListController(getCoastService(), getSceneNavigator()));

         controllerFactories.put(CoastFormController.class, () ->
                 new CoastFormController(getCoastService(), getCountryService(), getSceneNavigator()));
    }

    public Object getController(Class<?> controllerClass) {
        Supplier<?> factory = controllerFactories.get(controllerClass);
        if (factory == null) {
            throw new IllegalArgumentException("Kontroler nije registriran u ApplicationContext-u: " + controllerClass.getName());
        }
        return factory.get();
    }

    public AuthService getAuthService() { return services.getAuthService(); }
    public InstructorService getInstructorService() { return services.getInstructorService(); }
    public SurfSpotService getSurfSpotService() { return services.getSurfSpotService(); }
    public SurfingSchoolService getSurfingSchoolService() { return services.getSurfingSchoolService(); }
    public CoastService getCoastService() { return services.getCoastService(); }
    public CountryService getCountryService() { return services.getCountryService(); }
    public UserSession getSession() { return session; }
    public SceneNavigator getSceneNavigator() { return navigator; }

    public boolean isAuthenticated() {
        return session.isAuthenticated();
    }

    public void setPrimaryStage(Stage stage) { this.primaryStage = stage; }
    public Stage getPrimaryStage() { return primaryStage; }
}