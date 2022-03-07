package com.vinhderful.raytracer.misc;

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
            System.out.println("Loading Skybox Image '" + resourceName + "'...");
            sphereImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(resourceName)));
            System.out.println("Skybox Ready!");
        } catch (IOException | IllegalArgumentException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    public int[] getARGB() {

        int width = sphereImage.getWidth();
        int height = sphereImage.getHeight();

        int[] argb = new int[width * height];

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                argb[x + y * width] = sphereImage.getRGB(x, y);

        return argb;
    }

    public int[] getDimensions() {
        return new int[]{sphereImage.getWidth(), sphereImage.getHeight()};
    }
}
