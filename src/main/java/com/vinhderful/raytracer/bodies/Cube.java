package com.vinhderful.raytracer.bodies;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

/**
 * Represent a sphere in a 3D scene using its position, radius and color
 */
public class Cube extends Body {

    private final Vector3f min;
    private final Vector3f max;

    public Cube(Vector3f position, float scale, Color color, float reflectivity) {
        super(position, color, reflectivity);
        this.max = position.add(scale * 0.5F);
        this.min = position.subtract(scale * 0.5F);
    }

    @Override
    public Vector3f getIntersection(Ray ray) {
        float t1, t2, tnear = Float.NEGATIVE_INFINITY, tfar = Float.POSITIVE_INFINITY, temp;
        boolean intersectFlag = true;
        float[] rayDirection = ray.getDirection().toArray();
        float[] rayOrigin = ray.getOrigin().toArray();
        float[] b1 = min.toArray();
        float[] b2 = max.toArray();

        for (int i = 0; i < 3; i++) {
            if (rayDirection[i] == 0) {
                if (rayOrigin[i] < b1[i] || rayOrigin[i] > b2[i])
                    intersectFlag = false;
            } else {
                t1 = (b1[i] - rayOrigin[i]) / rayDirection[i];
                t2 = (b2[i] - rayOrigin[i]) / rayDirection[i];
                if (t1 > t2) {
                    temp = t1;
                    t1 = t2;
                    t2 = temp;
                }
                if (t1 > tnear)
                    tnear = t1;
                if (t2 < tfar)
                    tfar = t2;
                if (tnear > tfar)
                    intersectFlag = false;
                if (tfar < 0)
                    intersectFlag = false;
            }
        }
        if (intersectFlag)
            return ray.getOrigin().add(ray.getDirection().multiply(tnear));
        else
            return null;
    }

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
