package com.vinhderful.pathtracer.bodies;

import com.vinhderful.pathtracer.utils.Color;
import com.vinhderful.pathtracer.utils.Ray;
import com.vinhderful.pathtracer.utils.Vector3f;

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
     * https://gamedev.stackexchange.com/questions/18436/most-efficient-aabb-vs-ray-collision-algorithms
     *
     * @param ray the ray
     * @return The first point of intersection as Vector3f or null if the ray does
     * not intersect with this cube
     */
    @Override
    public Vector3f getIntersection(Ray ray) {

        float t1, t2, tNear = Float.NEGATIVE_INFINITY, tFar = Float.POSITIVE_INFINITY;
        boolean intersects = true;

        float[] rayDirection = ray.getDirection().toArray();
        float[] rayOrigin = ray.getOrigin().toArray();
        float[] b1 = min.toArray();
        float[] b2 = max.toArray();

        for (int i = 0; i < 3; i++) {
            if (rayDirection[i] == 0) {
                if (rayOrigin[i] < b1[i] || rayOrigin[i] > b2[i])
                    intersects = false;
            } else {
                t1 = (b1[i] - rayOrigin[i]) / rayDirection[i];
                t2 = (b2[i] - rayOrigin[i]) / rayDirection[i];


                if (t1 > t2) {
                    float temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                if (t1 > tNear) tNear = t1;
                if (t2 < tFar) tFar = t2;
                if (tNear > tFar || tFar < 0) intersects = false;
            }
        }

        if (intersects) return ray.getOrigin().add(ray.getDirection().multiply(tNear));
        else return null;
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
