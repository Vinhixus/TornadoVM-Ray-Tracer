package com.vinhderful.raytracer;

import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.renderer.World;
import com.vinhderful.raytracer.shapes.Sphere;
import com.vinhderful.raytracer.utils.Vector3f;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Controller {

    @FXML
    public Pane pane;

    @FXML
    public Canvas canvas;

    @FXML
    public void initialize() {
        Renderer renderer = new Renderer(canvas.getGraphicsContext2D());
        World world = new World();

        Sphere sphere3 = new Sphere(new Vector3f(-1F, 0, 0.5F), 0.4f, Color.RED);
        Sphere sphere1 = new Sphere(new Vector3f(0, 0, 1), 0.4f, Color.GREEN);
        Sphere sphere2 = new Sphere(new Vector3f(1F, 0, 2), 0.4f, Color.BLUE);
        world.addShape(sphere1);
        world.addShape(sphere2);
        world.addShape(sphere3);

        renderer.render(world);
    }
}
