package com.vinhderful.pathtracer.misc;

import com.vinhderful.pathtracer.utils.Color;
import uk.ac.manchester.tornado.api.collections.types.VectorFloat4;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents a spherical skybox initialised by image
 */
public class Skybox {

    private BufferedImage image;
    private VectorFloat4 buffer;

    /**
     * Read the given resource into a BufferedImage
     *
     * @param resourceName the path to the resource
     */
    public Skybox(String resourceName) {

        image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(resourceName)));
        } catch (IOException | IllegalArgumentException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        allocateAndInitializeBuffer();
    }

    /**
     * Allocate memory space and initialise the input buffer
     */
    private void allocateAndInitializeBuffer() {

        int width = image.getWidth();
        int height = image.getHeight();

        buffer = new VectorFloat4(width * height);

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                buffer.set(x + y * width, Color.toFloat4(image.getRGB(x, y)));
    }

    /**
     * Return the memory address of the input buffer
     *
     * @return the pointer pointing to the VectorFloat4 input buffer
     */
    public VectorFloat4 getBuffer() {
        return buffer;
    }

    /**
     * Return the dimensions as an input buffer
     *
     * @return the dimensions int array containing [0] = width, [1] = height
     */
    public int[] getDimensionsBuffer() {
        return new int[]{image.getWidth(), image.getHeight()};
    }
}
