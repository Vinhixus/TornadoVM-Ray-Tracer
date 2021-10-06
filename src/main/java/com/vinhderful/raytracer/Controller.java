package com.vinhderful.raytracer;

import com.vinhderful.raytracer.renderer.Camera;
import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.renderer.World;
import com.vinhderful.raytracer.shapes.Sphere;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

public class Controller {

    @FXML
    public Pane pane;
    public Canvas canvas;
    public Slider camX;
    public Slider camY;
    public Slider camZ;
    public Slider camYaw;
    public Slider camPitch;

    @FXML
    public void initialize() {
        Renderer renderer = new Renderer(canvas.getGraphicsContext2D());
        World world = new World(Color.BLACK);
        Camera camera = new Camera();

        Sphere sphere3 = new Sphere(new Vector3f(-1.5F, 0, 1F), 0.5f, Color.RED, 8F);
        Sphere sphere1 = new Sphere(new Vector3f(0, 0, 1F), 0.5f, Color.GREEN, 16F);
        Sphere sphere2 = new Sphere(new Vector3f(1.5F, 0, 1F), 0.5f, Color.BLUE, 32F);
        world.addShape(sphere1);
        world.addShape(sphere2);
        world.addShape(sphere3);

        camX.valueProperty().addListener((observable, oldValue, newValue) -> camera.setX(newValue.floatValue()));
        camY.valueProperty().addListener((observable, oldValue, newValue) -> camera.setY(newValue.floatValue()));
        camZ.valueProperty().addListener((observable, oldValue, newValue) -> camera.setZ(newValue.floatValue()));
        camYaw.valueProperty().addListener((observable, oldValue, newValue) -> camera.setYaw(newValue.floatValue()));
        camPitch.valueProperty().addListener((observable, oldValue, newValue) -> camera.setPitch(newValue.floatValue()));

        // renderer.render(world, camera);

        AnimationTimer timer = new AnimationTimer() {
            float time = 0;

            @Override
            public void handle(long now) {
                time = (time + 0.2F) % 360;
                world.setLightX((float) Math.cos(time) * 2F);
                world.setLightZ((float) Math.sin(time) * 2F);

                renderer.render(world, camera);
            }
        };
        timer.start();
    }
}
