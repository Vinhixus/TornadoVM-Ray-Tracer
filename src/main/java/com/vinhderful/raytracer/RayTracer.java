package com.vinhderful.raytracer;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
        stage.getIcons().add(new Image(Objects.requireNonNull(RayTracer.class.getResourceAsStream("icon.png"))));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}