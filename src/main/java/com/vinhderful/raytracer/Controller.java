package com.vinhderful.raytracer;

import com.vinhderful.raytracer.bodies.Cube;
import com.vinhderful.raytracer.bodies.Sphere;
import com.vinhderful.raytracer.renderer.Renderer;
import com.vinhderful.raytracer.scene.World;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import static javafx.embed.swing.SwingFXUtils.fromFXImage;

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

        Sphere sphereWhite = new Sphere(new Vector3f(-3F, -0.5F, 0), 0.5F, Color.WHITE, 4F);
        Sphere sphereRed = new Sphere(new Vector3f(-1.5F, -0.5F, 0), 0.5F, Color.RED, 8F);
        Sphere sphereGreen = new Sphere(new Vector3f(0, -0.5F, 0), 0.5F, Color.GREEN, 16F);
        Sphere sphereBlue = new Sphere(new Vector3f(1.5F, -0.5F, 0), 0.5F, Color.BLUE, 32F);
        Sphere sphereBlack = new Sphere(new Vector3f(3F, -0.5F, 0), 0.5F, Color.BLACK, 48F);
        world.addBody(sphereWhite);
        world.addBody(sphereRed);
        world.addBody(sphereGreen);
        world.addBody(sphereBlue);
        world.addBody(sphereBlack);

        Cube cube = new Cube(new Vector3f(1.5F, 0, 2.5F), 2F, new Color(0.35F, 0.35F, 0.35F), 32F);
        world.addBody(cube);

        // Render world on canvas
        System.out.println("---------------------------------------");
        System.out.println("Rendering scene...");
        renderer.render(world);
        System.out.println("Scene rendered.");
        System.out.println("---------------------------------------");

        // Write result to file
        System.out.println("Writing image to Render.png...");

        File file = new File("Render.png");
        try {
            WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, writableImage);
            RenderedImage renderedImage = fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error!");
        }

        System.out.println("Done!");
        System.out.println("---------------------------------------");
        System.exit(0);
    }
}
