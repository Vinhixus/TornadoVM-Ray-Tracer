package com.vinhderful.raytracer.renderer;

import java.nio.IntBuffer;

import com.vinhderful.raytracer.shapes.Shape;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;

public class Renderer {

    private static final float AMBIENT_STRENGTH = 0.1F;

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
                Vector3f rayDir = Vector3f.rotate(Vector3f.normalize(new Vector3f(nsc[0], nsc[1], 0).subtract(eyePos)), camera.getYaw(), camera.getPitch());
                Ray ray = new Ray(eyePos.add(camera.getPosition()), rayDir);

                Hit hit = getClosestHit(ray, world);
                if (hit != null && (hit.getShape().equals(world.getLight()) || hit.getShape().equals(world.getPlane())))
                    pixels[x + y * width] = hit.getColor().toARGB();
                else
                    pixels[x + y * width] = getAmbient(hit, world).add(getDiffuse(hit, world)).toARGB();
            }

        pixelWriter.setPixels(0, 0, width, height, format, pixels, 0, width);
    }

    public static Hit getClosestHit(Ray ray, World world) {
        Hit closestHit = null;

        for (Shape shape : world.getShapes()) {
            if (shape == null)
                continue;

            Vector3f intersection = shape.getIntersection(ray);
            if (intersection != null && (closestHit == null || Vector3f.distance(closestHit.getPosition(), ray.getOrigin()) > Vector3f.distance(intersection, ray.getOrigin())))
                closestHit = new Hit(ray, shape, intersection);
        }

        return closestHit;
    }

    public static Color getAmbient(Hit hit, World world) {
        if (hit != null) {
            Color shapeColor = hit.getColor();
            Color lightColor = world.getLight().getColor();
            return shapeColor.multiply(lightColor).multiply(AMBIENT_STRENGTH);
        }

        return world.getBackgroundColor();
    }

    public static Color getDiffuse(Hit hit, World world) {
        if (hit != null) {
            Light light = world.getLight();
            Color lightColor = light.getColor();
            Color shapeColor = hit.getColor();

            float diffuseBrightness = Math.max(0F, Vector3f.dotProduct(hit.getNormal(), light.getPosition().subtract(hit.getPosition())));
            return shapeColor.multiply(lightColor).multiply(diffuseBrightness);
        }

        return world.getBackgroundColor();
    }
}
