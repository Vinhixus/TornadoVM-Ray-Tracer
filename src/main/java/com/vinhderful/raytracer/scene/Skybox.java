package com.vinhderful.raytracer.scene;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Skybox {

    private BufferedImage sphereImage;

    public Skybox(String resourceName) {

        sphereImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        
        try {
            System.out.println("Loading skybox " + resourceName + "...");
            sphereImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(resourceName)));
            System.out.println("Skybox ready.");
        } catch (IOException | IllegalArgumentException ex) {
            try {
                sphereImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("error_skybox.jpg")));
            } catch (IOException | IllegalArgumentException ex2) {
                ex2.printStackTrace();
                System.exit(-1);
            }

            ex.printStackTrace();
        }
    }

    public Color getColor(Vector3f d) {

        // Convert unit vector to texture coordinates
        float u = (float) (0.5 + Math.atan2(d.getZ(), d.getX()) / (2 * Math.PI));
        float v = (float) (0.5 - Math.asin(d.getY()) / Math.PI);

        try {
            return Color.fromInt(sphereImage.getRGB((int) (u * (sphereImage.getWidth() - 1)), (int) (v * (sphereImage.getHeight() - 1))));
        } catch (Exception e) {
            System.out.println("U: " + u + " V: " + v);
            e.printStackTrace();

            return Color.MAGENTA;
        }
    }
}
