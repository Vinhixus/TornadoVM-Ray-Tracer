package com.vinhderful.raytracer.scene;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents a spherical skybox initialised by image
 */
public class Skybox {

    private BufferedImage sphereImage;

    /**
     * Read the given resource into a BufferedImage
     *
     * @param resourceName the path to the resource
     */
    public Skybox(String resourceName) {

        sphereImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

        try {
            System.out.println("Loading skybox image '" + resourceName + "'...");
            sphereImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(resourceName)));
            System.out.println("Skybox ready.");
        } catch (IOException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the color of the skybox at a certain point on the surface
     * given by the direction pointing from the origin of the sphere to the surface point
     *
     * @param d the direction
     * @return the color of the skybox at the specified point
     */
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
