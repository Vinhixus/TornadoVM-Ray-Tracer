package com.vinhderful.pathtracer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Java-based Path Tracer using JavaFX
 */
public class App extends Application {

    /**
     * Launch application window
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initialise application window
     *
     * @param stage the stage to show
     */
    @Override
    public void start(Stage stage) {

        StackPane root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("controllers/Loading.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = null;
        if (root != null) scene = new Scene(root);

        stage.setTitle("TornadoVM Path Tracer");
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("icon.png"))));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Exit handler
     */
    @Override
    public void stop() {
        System.out.println("Exiting application...");
        System.exit(0);
    }
}