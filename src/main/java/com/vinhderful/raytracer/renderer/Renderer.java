package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.shapes.Shape;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Renderer {

    private final GraphicsContext g;
    private final PixelWriter pixelWriter;
    private final int width;
    private final int height;

    public Renderer(GraphicsContext g) {
        this.g = g;
        this.pixelWriter = g.getPixelWriter();
        this.width = (int) g.getCanvas().getWidth();
        this.height = (int) g.getCanvas().getHeight();
    }

    public static float[] getNormalizedScreenCoordinates(int x, int y, int width, int height) {
        float u,v;
        if (width > height) {
            u = (float) (x - width / 2 + height / 2) / height * 2 - 1;
            v = -((float) y / height * 2 - 1);
        } else {
            u = (float) x / width * 2 - 1;
            v = -((float) (y - height / 2 + width / 2) / width * 2 - 1);
        }

        return new float[] { u, v };
    }

    public void render(World world) {

        g.setFill(Color.BLACK);
        g.fillRect(0, 0, width, height);

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {

                float[] nsc = getNormalizedScreenCoordinates(x, y, width, height);
                Ray ray = new Ray(new Vector3f(nsc[0], nsc[1], 0), new Vector3f(0, 0, 1));

                for (Shape shape : world.getShapes()) {
                    Vector3f intersection = shape.calculateIntersection(ray);

                    if (intersection != null)
                        pixelWriter.setColor(x, y, shape.getColor());
                }
            }
    }
}
