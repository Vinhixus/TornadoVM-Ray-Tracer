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

    private static final float AMBIENT_STRENGTH = 0.05F;
    private static final float SPECULAR_STRENGTH = 0.5F;

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
                Ray ray = new Ray(eyePos.add(camera.getPosition()), rayDir);

                Hit hit = getClosestHit(ray, world);
                if (hit != null) {
                    if (hit.getShape().equals(world.getLight()) || hit.getShape().equals(world.getPlane()))
                        pixels[x + y * width] = hit.getColor().toARGB();
                    else
                        pixels[x + y * width] = getPhong(hit, world).toARGB();
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

    public static Color getPhong(Hit hit, World world) {
        return getAmbient(hit, world).add(getDiffuse(hit, world)).add(getSpecular(hit, world));
    }

    public static Color getAmbient(Hit hit, World world) {
        Color shapeColor = hit.getColor();
        Color lightColor = world.getLight().getColor();
        return shapeColor.multiply(lightColor).multiply(AMBIENT_STRENGTH);
    }

    public static Color getDiffuse(Hit hit, World world) {
        Light light = world.getLight();
        Color lightColor = light.getColor();
        Color shapeColor = hit.getColor();

        float diffuseBrightness = Math.max(0F, hit.getNormal().dotProduct(light.getPosition().subtract(hit.getPosition())));
        return shapeColor.multiply(lightColor).multiply(diffuseBrightness);
    }

    private static Color getSpecular(Hit hit, World world) {
        Camera camera = world.getCamera();
        Light light = world.getLight();
        Color lightColor = light.getColor();
        Vector3f hitPos = hit.getPosition();
        Vector3f cameraDirection = hitPos.subtract(camera.getPosition()).normalize();
        Vector3f lightDirection = light.getPosition().subtract(hitPos).normalize();
        Vector3f reflectionVector = lightDirection.subtract(hit.getNormal().multiply(2 * lightDirection.dotProduct(hit.getNormal())));

        float specularFactor = Math.max(0F, reflectionVector.dotProduct(cameraDirection));
        float specularBrightness = (float) Math.pow(specularFactor, hit.getShape().getReflectivity());
        return lightColor.multiply(specularBrightness).multiply(SPECULAR_STRENGTH);
    }
}
