package hr.algebra.surfspot.context;

import hr.algebra.surfspot.controller.MainLayoutController;
import hr.algebra.surfspot.controller.auth.AuthLayoutController;
import hr.algebra.surfspot.controller.auth.LoginController;
import hr.algebra.surfspot.controller.auth.RegisterController;
import hr.algebra.surfspot.controller.coast.CoastFormController;
import hr.algebra.surfspot.controller.coast.CoastListController;
import hr.algebra.surfspot.controller.country.CountryFormController;
import hr.algebra.surfspot.controller.country.CountryListController;
import hr.algebra.surfspot.controller.instructor.InstructorFormController;
import hr.algebra.surfspot.controller.instructor.InstructorListController;
import hr.algebra.surfspot.controller.school.SurfingSchoolFormController;
import hr.algebra.surfspot.controller.school.SurfingSchoolListController;
import hr.algebra.surfspot.controller.surfspot.SurfSpotFormController;
import hr.algebra.surfspot.controller.surfspot.SurfSpotListController;
import hr.algebra.surfspot.controller.user.UserFormController;
import hr.algebra.surfspot.controller.user.UserListController;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ControllerFactory {
    private final Map<Class<?>, Supplier<?>> controllerFactories = new HashMap<>();

    public ControllerFactory(ServiceFactory services, UserSession session, SceneNavigator navigator) {
        controllerFactories.put(LoginController.class, () ->
                new LoginController(services.getAuthService(), session, navigator));

        controllerFactories.put(RegisterController.class, () ->
                new RegisterController(services.getAuthService(), session, navigator));

        controllerFactories.put(AuthLayoutController.class, () ->
                new AuthLayoutController(navigator));

        controllerFactories.put(MainLayoutController.class, () ->
                new MainLayoutController(navigator, session));

        controllerFactories.put(InstructorListController.class, () ->
                new InstructorListController(services.getInstructorService(), navigator));

        controllerFactories.put(InstructorFormController.class, () ->
                new InstructorFormController(services.getInstructorService(), services.getSurfingSchoolService(), navigator));

        controllerFactories.put(SurfSpotListController.class, () ->
                new SurfSpotListController(services.getSurfSpotService(), navigator));

        controllerFactories.put(SurfSpotFormController.class, () ->
                new SurfSpotFormController(services.getSurfSpotService(), services.getCoastService(), navigator));

        controllerFactories.put(SurfingSchoolListController.class, () ->
                new SurfingSchoolListController(services.getSurfingSchoolService(), navigator));

        controllerFactories.put(SurfingSchoolFormController.class, () ->
                new SurfingSchoolFormController(services.getSurfingSchoolService(), services.getSurfSpotService(), navigator));

        controllerFactories.put(CoastListController.class, () ->
                new CoastListController(services.getCoastService(), navigator));

        controllerFactories.put(CoastFormController.class, () ->
                new CoastFormController(services.getCoastService(), services.getCountryService(), navigator));

        controllerFactories.put(CountryListController.class, () ->
                new CountryListController(services.getCountryService(), navigator));

        controllerFactories.put(CountryFormController.class, () ->
                new CountryFormController(services.getCountryService(), navigator));

        controllerFactories.put(UserListController.class, () ->
                new UserListController(services.getUserService(), navigator));

        controllerFactories.put(UserFormController.class, () ->
                new UserFormController(services.getUserService(), navigator));
    }

    public Object getController(Class<?> controllerClass) {
        Supplier<?> factory = controllerFactories.get(controllerClass);
        if (factory == null) {
            throw new IllegalArgumentException("Kontroler nije registriran u ControllerFactory-u: " + controllerClass.getName());
        }
        return factory.get();
    }
}