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
     * Calculate the OpenGL style x coordinate of the canvas
     *
     * @param x the pixel's x coordinate
     * @return the normalized x coordinate
     */
    public float getNormalizedX(int x) {
        if (width > height)
            return (float) (x - width / 2 + height / 2) / height * 2 - 1;
        else
            return (float) x / width * 2 - 1;
    }

    /**
     * Calculate the OpenGL style y coordinate of the canvas
     *
     * @param y the pixel's y coordinate
     * @return the normalized y coordinate
     */
    public float getNormalizedY(int y) {
        if (width > height)
            return -((float) y / height * 2 - 1);
        else
            return -((float) (y - height / 2 + width / 2) / width * 2 - 1);
    }

    /**
     * Render the scene given the world to render
     *
     * @param world the world to render
     */
    public void render(World world) {
        Camera camera = world.getCamera();
        Vector3f eyePos = new Vector3f(0, 0, (float) (-1 / Math.tan(Math.toRadians(camera.getFOV() / 2))));

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                Vector3f rayDir = new Vector3f(getNormalizedX(x), getNormalizedY(y), 0).subtract(eyePos).normalize().rotate(camera.getYaw(), camera.getPitch());
                Ray ray = new Ray(camera.getPosition(), rayDir);

                Hit hit = getClosestHit(ray, world);
                if (hit != null) {
                    if (hit.getBody().equals(world.getLight()))
                        pixels[x + y * width] = hit.getColor().toARGB();
                    else
                        pixels[x + y * width] = Shader.getPhong(hit, world).add(Shader.getReflection(hit, world, 6)).multiply(Shader.getShadowFactor(hit, world)).toARGB();
                } else {
                    pixels[x + y * width] = world.getBackgroundColor().toARGB();
                }
            }

        pixelWriter.setPixels(0, 0, width, height, format, pixels, 0, width);
    }
}
