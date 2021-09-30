package com.vinhphamvan.raytracer;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Controller {

    @FXML
    public Pane pane;

    @FXML
    public Canvas canvas;

    private GraphicsContext g;
    private PixelWriter pixelWriter;

    @FXML
    public void initialize() {
        g = canvas.getGraphicsContext2D();
        pixelWriter = g.getPixelWriter();

        int x = (int) canvas.getWidth();
        int y = (int) canvas.getHeight();

        for (int i = 0; i < x; i++)
            for (int j = 0; j < y; j++)
                pixelWriter.setColor(i, j, Color.BLACK);
    }
}
