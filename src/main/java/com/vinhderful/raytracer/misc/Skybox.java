package com.vinhderful.raytracer.misc;

import uk.ac.manchester.tornado.api.collections.types.Float4;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

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

    public static Float4 fromARGB(int argb) {
        int b = (argb) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        return new Float4(r / 255F, g / 255F, b / 255F, 0);
    }

    public VectorFloat4 getVectorFloat4() {

        int width = sphereImage.getWidth();
        int height = sphereImage.getHeight();

        VectorFloat4 colors = new VectorFloat4(width * height);

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                colors.set(x + y * width, fromARGB(sphereImage.getRGB(x, y)));

        return colors;
    }

    public int[] getDimensions() {
        return new int[]{sphereImage.getWidth(), sphereImage.getHeight()};
    }
}
