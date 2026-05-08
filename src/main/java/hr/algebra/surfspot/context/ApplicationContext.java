package hr.algebra.surfspot.context;

import hr.algebra.surfspot.controller.auth.LoginController;
import hr.algebra.surfspot.controller.auth.RegisterController;
import hr.algebra.surfspot.service.AuthService;
import javafx.stage.Stage;

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
        this.services = new ServiceRegistry();
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
                new LoginController(getAuthService(), getSession(), getNavigator()));

        controllerFactories.put(RegisterController.class, () ->
                new RegisterController(getAuthService(), getSession(), getNavigator()));
    }

    public Object getController(Class<?> controllerClass) {
        Supplier<?> factory = controllerFactories.get(controllerClass);
        if (factory != null) {
            return factory.get();
        }
        try {
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Neuspjelo stvaranje kontrolera: " + controllerClass.getName(), e);
        }
    }

    public AuthService getAuthService() { return services.getAuthService(); }
    public UserSession getSession() { return session; }
    public SceneNavigator getNavigator() { return navigator; }

    public boolean isAuthenticated() {
        return session.isAuthenticated();
    }

    public void setPrimaryStage(Stage stage) { this.primaryStage = stage; }
    public Stage getPrimaryStage() { return primaryStage; }
}