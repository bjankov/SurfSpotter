package hr.algebra.surfspot.context;

import hr.algebra.surfspot.controller.auth.AuthLayoutController;
import hr.algebra.surfspot.controller.coast.CoastFormController;
import hr.algebra.surfspot.controller.instructor.InstructorFormController;
import hr.algebra.surfspot.controller.school.SurfingSchoolFormController;
import hr.algebra.surfspot.controller.surfspot.SurfSpotFormController;
import hr.algebra.surfspot.model.Coast;
import hr.algebra.surfspot.model.Instructor;
import hr.algebra.surfspot.model.SurfSpot;
import hr.algebra.surfspot.model.SurfingSchool;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

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

    private void loadAuthLayout(String formFxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/main_auth_container.fxml"));
            loader.setControllerFactory(context::getController);

            Parent root = loader.load();
            AuthLayoutController layoutController = loader.getController();
            layoutController.loadForm(formFxmlPath);

            Stage stage = context.getPrimaryStage();
            stage.setTitle("SurfSpot - " + title);
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Greška pri učitavanju AuthLayout-a", e);
        }
    }

    public void navigateToMain() {
        if (!context.isAuthenticated()) {
            navigateToLogin();
            return;
        }

        try {
            Parent root = loadFXML("/fxml/main_layout.fxml");
            Stage stage = context.getPrimaryStage();
            stage.setTitle("SurfSpot - Aplikacija");
            stage.setScene(new Scene(root, 1024, 768));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Greška pri učitavanju Main ekrana", e);
        }
    }

    public void setMainContentArea(StackPane contentArea) {
        this.mainContentArea = contentArea;
    }

    private void displayInMain(Node node) {
        if (mainContentArea == null) {
            throw new IllegalStateException("MainContentArea nije postavljen. Ne mogu navigirati unutar glavnog prozora.");
        }
        mainContentArea.getChildren().clear();
        mainContentArea.getChildren().add(node);
    }

    public void navigateToSurfSpotList() {
        try {
            Parent listNode = loadFXML("/fxml/surf_spot/surf_spot_list.fxml");
            displayInMain(listNode);
        } catch (IOException e) {
            throw new RuntimeException("Greska pri ucitavanju liste surf spotova", e);
        }
    }

    public void navigateToSurfSpotForm(SurfSpot spot) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/surf_spot/surf_spot_form.fxml"));
            loader.setControllerFactory(context::getController);
            Parent formNode = loader.load();

            SurfSpotFormController controller = loader.getController();
            controller.setSurfSpot(spot);

            displayInMain(formNode);
        } catch (IOException e) {
            throw new RuntimeException("Greška pri otvaranju forme surf spota", e);
        }
    }

    public void navigateToInstructorList() {
        try {
            Parent listNode = loadFXML("/fxml/instructor/instructor_list.fxml");
            displayInMain(listNode);
        } catch (IOException e) {
            throw new RuntimeException("Greška pri učitavanju liste instruktora", e);
        }
    }

    public void navigateToInstructorForm(Instructor instructor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/instructor/instructor_form.fxml"));
            loader.setControllerFactory(context::getController);
            Parent formNode = loader.load();

            InstructorFormController controller = loader.getController();
            controller.setInstructor(instructor);

            displayInMain(formNode);
        } catch (IOException e) {
            throw new RuntimeException("Greška pri otvaranju forme instruktora", e);
        }
    }

    public void navigateToSurfingSchoolList() {
        try {
            Parent listNode = loadFXML("/fxml/surfing_school/surfing_school_list.fxml");
            displayInMain(listNode);
        } catch (IOException e) {
            throw new RuntimeException("Greška pri učitavanju liste škola surfanja", e);
        }
    }

    public void navigateToSurfingSchoolForm(SurfingSchool school) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/surfing_school/surfing_school_form.fxml"));
            loader.setControllerFactory(context::getController);
            Parent formNode = loader.load();

            SurfingSchoolFormController controller = loader.getController();
            controller.setSurfingSchool(school);

            displayInMain(formNode);
        } catch (IOException e) {
            throw new RuntimeException("Greška pri otvaranju forme skole", e);
        }
    }

    public void navigateToCoastForm(Coast coast) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/coast/coast_form.fxml"));
            loader.setControllerFactory(context::getController);
            Parent formNode = loader.load();

            CoastFormController controller = loader.getController();
            controller.setCoast(coast);

            displayInMain(formNode);
        } catch (IOException e) {
            throw new RuntimeException("Greška pri otvaranju forme obala", e);
        }
    }

    public void navigateToCoastList() {
        try {
            Parent listNode = loadFXML("/fxml/coast/coast_list.fxml");
            displayInMain(listNode);
        } catch (IOException e) {
            throw new RuntimeException("Greška pri učitavanju liste obala", e);
        }
    }

    public Parent loadFXML(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(context::getController);
        return loader.load();
    }

}