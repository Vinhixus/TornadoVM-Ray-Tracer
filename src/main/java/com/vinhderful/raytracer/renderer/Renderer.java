package com.vinhderful.raytracer.renderer;

import java.nio.IntBuffer;

import com.vinhderful.raytracer.shapes.Shape;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;

public class Renderer {

    private final PixelWriter pixelWriter;
    private final int width;
    private final int height;

    private final int[] pixels;
    private final WritablePixelFormat<IntBuffer> format;

    public Renderer(GraphicsContext g) {
        this.pixelWriter = g.getPixelWriter();
        this.width = (int) g.getCanvas().getWidth();
        this.height = (int) g.getCanvas().getHeight();

        this.format = WritablePixelFormat.getIntArgbInstance();
        this.pixels = new int[width * height];
    }

    public static float[] getNormalizedCoordinates(int x, int y, int width, int height) {
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

    public void render(World world, Camera camera) {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {

                float[] nsc = getNormalizedCoordinates(x, y, width, height);

                Vector3f eyePos = new Vector3f(0, 0, (float) (-1 / Math.tan(Math.toRadians(camera.getFOV() / 2))));
                Vector3f rayDir = new Vector3f(nsc[0], nsc[1], 0).subtract(eyePos).normalize().rotate(camera.getYaw(), camera.getPitch());
                Ray ray = new Ray(camera.getPosition(), rayDir);

                Hit hit = getClosestHit(ray, world);
                if (hit != null) {
                    if (hit.getShape().equals(world.getLight()) || hit.getShape().equals(world.getPlane()))
                        pixels[x + y * width] = hit.getColor().toARGB();
                    else
                        pixels[x + y * width] = Shader.getPhong(hit, world).toARGB();
                } else {
                    pixels[x + y * width] = world.getBackgroundColor().toARGB();
                }
            }

        pixelWriter.setPixels(0, 0, width, height, format, pixels, 0, width);
    }

    public static Hit getClosestHit(Ray ray, World world) {
        Hit closestHit = null;

        for (Shape shape : world.getShapes()) {
            if (shape == null)
                continue;

            Vector3f intersection = shape.getIntersection(ray);
            if (intersection != null && (closestHit == null || closestHit.getPosition().distanceFrom(ray.getOrigin()) > intersection.distanceFrom(ray.getOrigin())))
                closestHit = new Hit(shape, intersection);
        }

        return closestHit;
    }
}
