package com.vinhderful.pathtracer.controllers;

import com.vinhderful.pathtracer.App;
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

public class Window {

    private volatile boolean isShown;
    private final String name;
    private final URL fxml;
    private final Image icon;

    public Window(String name, String fxmlPath, String iconPath) {
        this.name = name;
        this.fxml = Objects.requireNonNull(getClass().getResource(fxmlPath));
        this.icon = new Image(Objects.requireNonNull(App.class.getResourceAsStream(iconPath)));
    }

    private void load() {

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

        new Thread(loadTask).start();
    }

    public void show() {
        if (!isShown) {
            load();
            isShown = true;
        }
    }
}
