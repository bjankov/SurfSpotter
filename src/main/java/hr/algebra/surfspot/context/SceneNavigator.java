package hr.algebra.surfspot.context;

import hr.algebra.surfspot.controller.auth.AuthLayoutController;
import hr.algebra.surfspot.controller.coast.CoastFormController;
import hr.algebra.surfspot.controller.country.CountryFormController;
import hr.algebra.surfspot.controller.instructor.InstructorFormController;
import hr.algebra.surfspot.controller.school.SurfingSchoolFormController;
import hr.algebra.surfspot.controller.surfspot.SurfSpotFormController;
import hr.algebra.surfspot.controller.user.UserFormController;
import hr.algebra.surfspot.exception.ConfigurationException;
import hr.algebra.surfspot.model.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class SceneNavigator {

    private final ApplicationContext context;
    private StackPane mainContentArea;

    public SceneNavigator(ApplicationContext context) {
        this.context = context;
    }

    public void navigateToLogin() {
        loadAuthLayout("/fxml/auth/login.fxml", "Prijava");
    }

    public void navigateToRegister() {
        loadAuthLayout("/fxml/auth/register.fxml", "Registracija");
    }

    public void navigateToMain() {
        if (!context.isAuthenticated()) {
            navigateToLogin();
            return;
        }
        try {
            Stage stage = context.getPrimaryStage();
            stage.setTitle("Surf Spot Manager");
            stage.setScene(new Scene(loadFXML("/fxml/main_layout.fxml"), 1024, 768));
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            throw new ConfigurationException("Greška pri učitavanju Main ekrana", e);
        }
    }

    private void loadAuthLayout(String formFxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/main_auth_container.fxml"));
            loader.setControllerFactory(context::getController);
            Parent root = loader.load();

            loader.<AuthLayoutController>getController().loadForm(formFxmlPath);

            Stage stage = context.getPrimaryStage();
            stage.setTitle("SurfSpot - " + title);
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            throw new ConfigurationException("Greška pri učitavanju AuthLayout-a", e);
        }
    }

    public void navigateToSurfSpotList() {
        navigateTo("/fxml/surf_spot/surf_spot_list.fxml", "Greška pri učitavanju liste mjesta za surfanje");
    }

    public void navigateToSurfSpotForm(SurfSpot spot) {
        navigateToForm("/fxml/surf_spot/surf_spot_form.fxml", "Greška pri otvaranju forme mjesta za surfanje",
                (SurfSpotFormController c) -> c.setSurfSpot(spot));
    }

    public void navigateToInstructorList() {
        navigateTo("/fxml/instructor/instructor_list.fxml", "Greška pri učitavanju liste instruktora");
    }

    public void navigateToInstructorForm(Instructor instructor) {
        navigateToForm("/fxml/instructor/instructor_form.fxml", "Greška pri otvaranju forme instruktora",
                (InstructorFormController c) -> c.setInstructor(instructor));
    }

    public void navigateToSurfingSchoolList() {
        navigateTo("/fxml/surfing_school/surfing_school_list.fxml", "Greška pri učitavanju liste škola surfanja");
    }

    public void navigateToSurfingSchoolForm(SurfingSchool school) {
        navigateToForm("/fxml/surfing_school/surfing_school_form.fxml", "Greška pri otvaranju forme škole",
                (SurfingSchoolFormController c) -> c.setSurfingSchool(school));
    }

    public void navigateToCoastList() {
        navigateTo("/fxml/coast/coast_list.fxml", "Greška pri učitavanju liste obala");
    }

    public void navigateToCoastForm(Coast coast) {
        navigateToForm("/fxml/coast/coast_form.fxml", "Greška pri otvaranju forme obala",
                (CoastFormController c) -> c.setCoast(coast));
    }

    public void navigateToUserList() {
        navigateTo("/fxml/user/user_list.fxml", "Greška pri učitavanju liste korisnika");
    }

    public void navigateToUserForm(User user) {
        navigateToForm("/fxml/user/user_form.fxml", "Greška pri otvaranju forme korisnika",
                (UserFormController c) -> c.setUser(user));
    }

    public void navigateToCountryList() {
        navigateTo("/fxml/country/country_list.fxml", "Greška pri učitavanju liste država");
    }

    public void navigateToCountryForm(Country country) {
        navigateToForm("/fxml/country/country_form.fxml", "Greška pri otvaranju forme države",
                (CountryFormController c) -> c.setCountry(country));
    }

    private void navigateTo(String fxmlPath, String errorMessage) {
        try {
            displayInMain(loadFXML(fxmlPath));
        } catch (IOException e) {
            throw new ConfigurationException(errorMessage, e);
        }
    }

    private <T> void navigateToForm(String fxmlPath, String errorMessage, Consumer<T> setup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(context::getController);
            Parent node = loader.load();
            setup.accept(loader.getController());
            displayInMain(node);
        } catch (IOException e) {
            throw new ConfigurationException(errorMessage, e);
        }
    }

    public void setMainContentArea(StackPane contentArea) {
        this.mainContentArea = contentArea;
    }

    private void displayInMain(Node node) {
        if (mainContentArea == null) {
            throw new ConfigurationException("MainContentArea nije postavljen. Ne mogu navigirati unutar glavnog prozora.");
        }
        mainContentArea.getChildren().setAll(node);
    }

    public Parent loadFXML(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(context::getController);
        return loader.load();
    }
}