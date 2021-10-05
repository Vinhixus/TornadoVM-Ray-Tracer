package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.shapes.Shape;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;

public class Renderer {

    private static final float AMBIENT_STRENGTH = 0.1F;

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

        g.setFill(Color.BLACK.toPaint());
        g.fillRect(0, 0, width, height);

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {

                float[] nsc = getNormalizedCoordinates(x, y, width, height);

                Vector3f eyePos = new Vector3f(0, 0, (float) (-1 / Math.tan(Math.toRadians(camera.getFOV() / 2))));
                Vector3f rayDir = Vector3f.rotate(Vector3f.normalize(new Vector3f(nsc[0], nsc[1], 0).subtract(eyePos)), camera.getYaw(), camera.getPitch());
                Ray ray = new Ray(eyePos.add(camera.getPosition()), rayDir);

                Hit hit = getClosestHit(ray, world);
                pixelWriter.setColor(x, y, getPhong(hit, world).toPaint());
            }
    }

    public static Hit getClosestHit(Ray ray, World world) {
        Hit closestHit = null;

        for (Shape shape : world.getShapes()) {
            if (shape == null)
                continue;

            Vector3f hitPos = shape.getIntersection(ray);
            if (hitPos != null && (closestHit == null || Vector3f.distance(closestHit.getPosition(), ray.getOrigin()) > Vector3f.distance(hitPos, ray.getOrigin())))
                closestHit = new Hit(ray, shape, hitPos);
        }

        return closestHit;
    }

    public static Color getPhong(Hit hit, World world) {
        return getAmbient(hit, world).add(getDiffuse(hit, world));
    }

    public static Color getAmbient(Hit hit, World world) {
        if (hit != null)
            return hit.getShape().getColor().multiply(AMBIENT_STRENGTH);

        return world.getBackgroundColor();
    }

    public static Color getDiffuse(Hit hit, World world) {
        Light light = world.getLight();

        if (hit != null) {
            float diffuseBrightness = Math.max(0F, Math.min(1F, Vector3f.dotProduct(hit.getNormal(), light.getPosition().subtract(hit.getPosition()))));
            return hit.getShape().getColor().multiply(diffuseBrightness);
        }

        return world.getBackgroundColor();
    }
}
