package com.vinhphamvan.raytracer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RayTracer extends Application {

    @Override
    public void start(Stage stage) {

        BorderPane root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("GUI.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = null;
        if (root != null)
            scene = new Scene(root);

        stage.setTitle("TornadoVM Ray Tracer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}