package com.vinhderful.raytracer.controllers;

import com.vinhderful.raytracer.App;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Helper class to encapsulate Controls and About windows
 */
public class Window {

    private final String name;
    private final URL fxml;
    private final Image icon;
    private volatile boolean isShown;

    /**
     * Instantiate a Window given a title, ray to the controlling FXML and ray to the icon
     *
     * @param name    window title
     * @param fxmlRay ray to FXML
     * @param iconRay ray to icon
     */
    public Window(String name, String fxmlRay, String iconRay) {
        this.name = name;
        this.fxml = Objects.requireNonNull(getClass().getResource(fxmlRay));
        this.icon = new Image(Objects.requireNonNull(App.class.getResourceAsStream(iconRay)));
    }

    /**
     * Load the FXML and show the stage
     */
    private void load() {

        // Load FXML
        Task<Parent> loadTask = new Task<>() {

            @Override
            protected Parent call() {

                StackPane root = null;
                try {
                    root = FXMLLoader.load(fxml);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return root;
            }
        };

        // Show stage when FXML is loaded
        loadTask.setOnSucceeded(e -> Platform.runLater(() -> {
            StackPane root = (StackPane) loadTask.getValue();
            Scene scene = null;
            if (root != null) scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle(name);
            stage.getIcons().add(icon);
            stage.setResizable(false);
            stage.setScene(scene);

            stage.show();
            stage.setOnCloseRequest(event -> isShown = false);
        }));

        // Load FXML on new thread
        new Thread(loadTask).start();
    }

    /**
     * Show the window
     */
    public void show() {
        if (!isShown) {
            load();
            isShown = true;
        }
    }
}
