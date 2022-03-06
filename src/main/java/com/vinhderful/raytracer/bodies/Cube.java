package com.vinhderful.raytracer.bodies;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

/**
 * Represent a cube in a 3D scene using its position, scale and color
 */
public class Cube extends Body {

    private final float scale;
    private final Vector3f min;
    private final Vector3f max;

    /**
     * Constructs a Cube object given its position, scale and color.
     * Reflectivity will be set to the default Body value
     *
     * @param position the position
     * @param scale    the scale
     * @param color    the color
     */
    public Cube(Vector3f position, float scale, Color color) {
        super(position, color);
        this.scale = scale;
        this.max = position.add(scale * 0.5F);
        this.min = position.subtract(scale * 0.5F);
    }

    /**
     * Constructs a Cube object given its position, scale and color and reflectivity
     *
     * @param position     the position
     * @param scale        the scale
     * @param color        the color
     * @param reflectivity the reflectivity
     */
    public Cube(Vector3f position, float scale, Color color, float reflectivity) {
        super(position, color, reflectivity);
        this.scale = scale;
        this.max = position.add(scale * 0.5F);
        this.min = position.subtract(scale * 0.5F);
    }

    /**
     * Get the cube's scale
     *
     * @return the cube's scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Given a ray, return the point where the ray first intersects with the cube
     *
     * @param ray the ray
     * @return The first point of intersection as Vector3f or null if the ray does
     * not intersect with this cube
     */
    @Override
    public Vector3f getIntersection(Ray ray) {

        float t1 = (min.getX() - ray.getOrigin().getX()) / ray.getDirection().getX();
        float t2 = (max.getX() - ray.getOrigin().getX()) / ray.getDirection().getX();
        float t3 = (min.getY() - ray.getOrigin().getY()) / ray.getDirection().getY();
        float t4 = (max.getY() - ray.getOrigin().getY()) / ray.getDirection().getY();
        float t5 = (min.getZ() - ray.getOrigin().getZ()) / ray.getDirection().getZ();
        float t6 = (max.getZ() - ray.getOrigin().getZ()) / ray.getDirection().getZ();

        float tMin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tMax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        if (tMax < 0 || tMin > tMax) return null;

        if (tMin < 0)
            return ray.getOrigin().add(ray.getDirection().multiply(tMax));
        else
            return ray.getOrigin().add(ray.getDirection().multiply(tMin));
    }

    /**
     * Get the normal vector of the cube at a given point
     *
     * @param point the point
     * @return the normal vector at the given point
     */
    @Override
    public Vector3f getNormalAt(Vector3f point) {
        float[] direction = point.subtract(position).toArray();
        float biggestValue = Float.NaN;

        for (int i = 0; i < 3; i++)
            if (Float.isNaN(biggestValue) || biggestValue < Math.abs(direction[i]))
                biggestValue = Math.abs(direction[i]);

        if (biggestValue == 0) return new Vector3f(0, 0, 0);
        else
            for (int i = 0; i < 3; i++)
                if (Math.abs(direction[i]) == biggestValue) {
                    float[] normal = new float[]{0, 0, 0};
                    normal[i] = direction[i] > 0 ? 1 : -1;

                    return new Vector3f(normal[0], normal[1], normal[2]);
                }

        return new Vector3f(0, 0, 0);
    }
}
