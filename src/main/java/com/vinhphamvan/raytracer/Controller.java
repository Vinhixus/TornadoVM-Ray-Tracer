package com.vinhphamvan.raytracer;

import com.vinhphamvan.raytracer.shapes.Sphere;
import com.vinhphamvan.raytracer.utils.Ray;
import com.vinhphamvan.raytracer.utils.Vector3f;

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
        Sphere sphere = new Sphere(new Vector3f(0, 0, 1), 0.5f, Color.WHITE);

        g = canvas.getGraphicsContext2D();
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();

        g.setFill(Color.BLACK);
        g.fillRect(0, 0, width, height);

        pixelWriter = g.getPixelWriter();

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {

                float u;
                float v;

                if (width > height) {
                    u = (float) (x - width / 2 + height / 2) / height * 2 - 1;
                    v = -((float) y / height * 2 - 1);
                } else {
                    u = (float) x / width * 2 - 1;
                    v = -((float) (y - height / 2 + width / 2) / width * 2 - 1);
                }

                Ray ray = new Ray(new Vector3f(u, v, 0), new Vector3f(0, 0, 1));
                Vector3f intersection = sphere.calculateIntersection(ray);

                if (intersection != null)
                    pixelWriter.setColor(x, y, sphere.getColor());
            }

    }
}
