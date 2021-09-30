package com.vinhphamvan.raytracer;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Controller {

    @FXML
    public Pane pane;

    @FXML
    public Canvas canvas;

    private GraphicsContext g;

    @FXML
    public void initialize() {
        g = canvas.getGraphicsContext2D();
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
