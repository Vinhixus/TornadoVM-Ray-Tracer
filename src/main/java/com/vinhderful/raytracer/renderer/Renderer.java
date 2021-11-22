package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.bodies.Body;
import com.vinhderful.raytracer.scene.Camera;
import com.vinhderful.raytracer.scene.World;
import com.vinhderful.raytracer.utils.Hit;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import org.apache.commons.math3.util.Pair;

import java.nio.IntBuffer;

/**
 * Implements functions to draw the scene on the canvas
 */
public class Renderer {

    private final PixelWriter pixelWriter;
    private final int width;
    private final int height;

    private final int[] pixels;
    private final WritablePixelFormat<IntBuffer> format;

    /**
     * Construct a Renderer object given the graphics context to draw to
     *
     * @param g the graphics context to draw to
     */
    public Renderer(GraphicsContext g) {
        this.pixelWriter = g.getPixelWriter();
        this.width = (int) g.getCanvas().getWidth();
        this.height = (int) g.getCanvas().getHeight();

        this.format = WritablePixelFormat.getIntArgbInstance();
        this.pixels = new int[width * height];
    }

    /**
     * Given a ray and a world, get a hit event of the closest body the ray hits in
     * the world
     *
     * @param ray   the ray
     * @param world the world
     * @return A hit event containing the ray, the closest body and the hit
     * position, null if the ray does not hit any bodies
     */
    public static Hit getClosestHit(Ray ray, World world) {
        Hit closestHit = null;

        for (Body body : world.getBodies()) {
            if (body == null) continue;

            Vector3f intersection = body.getIntersection(ray);
            if (intersection != null && (closestHit == null
                    || closestHit.getPosition().distanceFrom(ray.getOrigin())
                    > intersection.distanceFrom(ray.getOrigin())))
                closestHit = new Hit(body, ray, intersection);
        }

        return closestHit;
    }

    /**
     * Calculate the OpenGL style coordinates of the canvas
     *
     * @param x the pixel's x coordinate
     * @param y the pixel's y coordinate
     * @return a pair containing the [x -> u, y -> v] normalized coordinates
     */
    public Pair<Float, Float> getNormalizedCoordinates(int x, int y) {
        float u, v;
        if (width > height) {
            u = (float) (x - width / 2 + height / 2) / height * 2 - 1;
            v = -((float) y / height * 2 - 1);
        } else {
            u = (float) x / width * 2 - 1;
            v = -((float) (y - height / 2 + width / 2) / width * 2 - 1);
        }

        return new Pair<>(u, v);
    }

    /**
     * Render the scene given the world to render
     *
     * @param world the world to render
     */
    public void render(World world) {
        Camera camera = world.getCamera();

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {

                Pair<Float, Float> nsc = getNormalizedCoordinates(x, y);

                Vector3f eyePos = new Vector3f(0, 0, (float) (-1 / Math.tan(Math.toRadians(camera.getFOV() / 2))));
                Vector3f rayDir = new Vector3f(nsc.getFirst(), nsc.getSecond(), 0).subtract(eyePos).normalize().rotate(camera.getYaw(), camera.getPitch());
                Ray ray = new Ray(camera.getPosition(), rayDir);

                Hit hit = getClosestHit(ray, world);
                if (hit != null) {
                    if (hit.getBody().equals(world.getLight()))
                        pixels[x + y * width] = hit.getColor().toARGB();
                    else
                        pixels[x + y * width] = Shader.getPhong(hit, world).add(Shader.getReflection(hit, world, 6)).toARGB();
                } else {
                    pixels[x + y * width] = world.getBackgroundColor().toARGB();
                }
            }

        pixelWriter.setPixels(0, 0, width, height, format, pixels, 0, width);
    }
}
