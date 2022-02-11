package com.vinhderful.raytracer;

import com.vinhderful.raytracer.bodies.Cube;
import com.vinhderful.raytracer.bodies.Sphere;
import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.scene.World;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

/**
 * Initialises JavaFX FXML elements together with GUI.fxml, contains driver code
 */
public class Controller {

    /**
     * Elements of the window
     */
    @FXML
    public Pane pane;
    public Canvas canvas;

    /**
     * Initialise renderer, world, camera and populate with objects
     */
    @FXML
    public void initialize() {

        Renderer renderer = new Renderer(canvas.getGraphicsContext2D());
        World world = new World();

        Sphere sphere3 = new Sphere(new Vector3f(-1.5F, 0, 0), 0.5f, Color.RED, 8F);
        Sphere sphere1 = new Sphere(new Vector3f(0, 0, 0), 0.5f, Color.GREEN, 16F);
        Sphere sphere2 = new Sphere(new Vector3f(1.5F, 0, 0), 0.5f, Color.BLUE, 32F);
        world.addBody(sphere1);
        world.addBody(sphere2);
        world.addBody(sphere3);

        Cube cube = new Cube(new Vector3f(1.5F, 0, 2.5F), 2F, new Color(0.35F, 0.35F, 0.35F), 64F);
        world.addBody(cube);

        renderer.render(world);
    }
}
