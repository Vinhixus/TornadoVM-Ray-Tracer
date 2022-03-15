package com.vinhderful.raytracer.bodies;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

/**
 * Represent a sphere in a 3D scene using its position, radius and color
 */
public class Sphere extends Body {

    private final float radius;

    /**
     * Constructs a Sphere object given its position, radius and color.
     * Reflectivity will be set to the default Body value
     *
     * @param position the position
     * @param radius   the radius
     * @param color    the color
     */
    public Sphere(Vector3f position, float radius, Color color) {
        super(position, color);
        this.radius = radius;
    }

    /**
     * Constructs a Sphere object given its position, radius, color and reflectivity
     *
     * @param position     the position
     * @param radius       the radius
     * @param color        the color
     * @param reflectivity the reflectivity
     */
    public Sphere(Vector3f position, float radius, Color color, float reflectivity) {
        super(position, color, reflectivity);
        this.radius = radius;
    }

    /**
     * Given a ray, return the point where the ray first intersects with the sphere
     * https://www.youtube.com/watch?v=HFPlKQGChpE&ab_channel=TheArtofCode
     *
     * @param ray the ray
     * @return The first point of intersection as Vector3f or null if the ray does
     * not intersect with this sphere
     */
    @Override
    public Vector3f getIntersection(Ray ray) {
        float t = position.subtract(ray.getOrigin()).dotProduct(ray.getDirection());
        Vector3f p = ray.getOrigin().add(ray.getDirection().multiply(t));

        float y = position.subtract(p).magnitude();
        if (y < radius) {
            float x = (float) Math.sqrt(radius * radius - y * y);
            float t1 = t - x;
            if (t1 > 0)
                return ray.getOrigin().add(ray.getDirection().multiply(t1));
            else
                return null;
        } else {
            return null;
        }
    }

    /**
     * Get the normal vector of the sphere at a given point
     *
     * @param point the point
     * @return the normal vector at the given point
     */
    @Override
    public Vector3f getNormalAt(Vector3f point) {
        return point.subtract(position).normalize();
    }
}
