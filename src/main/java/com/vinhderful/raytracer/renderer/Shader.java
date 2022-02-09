package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.scene.Light;
import com.vinhderful.raytracer.scene.World;
import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Hit;
import com.vinhderful.raytracer.utils.Ray;
import com.vinhderful.raytracer.utils.Vector3f;

/**
 * Implementations of the Phong shading techniques
 */
public class Shader {

    /**
     * Constant values to tweak the strengths of ambient and specular lighting
     */
    public static final float AMBIENT_STRENGTH = 0.05F;
    public static final float SPECULAR_STRENGTH = 0.5F;
    public static final float MAX_REFLECTIVITY = 256F;

    /**
     * Get the full Phong color of an object given a hit event and the world
     *
     * @param hit   the hit event
     * @param world the world
     * @return the result of the Phong shading
     */
    public static Color getPhong(Hit hit, World world) {
        return getAmbient(hit, world).add(getDiffuse(hit, world)).add(getSpecular(hit, world));
    }

    /**
     * Get the ambient color of a body given a hit event and the world
     *
     * @param hit   the hit event
     * @param world the world
     * @return the result of the ambient lighting
     */
    public static Color getAmbient(Hit hit, World world) {
        Color shapeColor = hit.getColor();
        Color lightColor = world.getLight().getColor();
        return shapeColor.multiply(lightColor).multiply(AMBIENT_STRENGTH);
    }

    /**
     * Get the diffuse color of a body given a hit event and the world
     *
     * @param hit   the hit event
     * @param world the world
     * @return the result of the diffuse lighting
     */
    public static Color getDiffuse(Hit hit, World world) {
        Light light = world.getLight();
        Color lightColor = light.getColor();
        Color shapeColor = hit.getColor();

        float diffuseBrightness = Math.max(0F, hit.getNormal().dotProduct(light.getPosition().subtract(hit.getPosition()).normalize()));
        return shapeColor.multiply(lightColor).multiply(diffuseBrightness);
    }

    /**
     * Get the specular highlights of a body given a hit event and the world
     *
     * @param hit   the hit event
     * @param world the world
     * @return the result of the specular highlights
     */
    public static Color getSpecular(Hit hit, World world) {
        Light light = world.getLight();
        Color lightColor = light.getColor();
        Vector3f hitPos = hit.getPosition();
        Vector3f rayDirection = hit.getRay().getDirection();
        Vector3f lightDirection = light.getPosition().subtract(hitPos).normalize();
        Vector3f reflectionVector = lightDirection.subtract(hit.getNormal().multiply(2 * lightDirection.dotProduct(hit.getNormal())));

        float specularFactor = Math.max(0F, reflectionVector.dotProduct(rayDirection));
        float specularBrightness = (float) Math.pow(specularFactor, hit.getBody().getReflectivity());
        return lightColor.multiply(specularBrightness).multiply(SPECULAR_STRENGTH);
    }

    /**
     * Get the factor that defines if a spot should be in shadow
     *
     * @param hit   the hit event
     * @param world the world
     * @return the shadow factor
     */
    public static float getShadowFactor(Hit hit, World world) {
        Light light = world.getLight();
        float lightScale = light.getScale();
        Vector3f lightPos = light.getPosition();
        Vector3f hitPos = hit.getPosition();

        int sample = 18;
        float uniform = lightScale * 2 / (sample - 1);

        int raysHit = 0;
        float totalRays = (float) (sample * sample * 1.3);

        for (float i = lightPos.getX() - lightScale; i <= lightPos.getX() + lightScale + 0.01F; i += uniform) {
            for (float j = lightPos.getZ() - lightScale; j <= lightPos.getZ() + lightScale + 0.01F; j += uniform) {
                Vector3f samplePoint = new Vector3f(i, lightPos.getY(), j);
                Vector3f rayDir = samplePoint.subtract(hitPos).normalize();
                Vector3f rayOrigin = hitPos.add(rayDir.multiply(0.001F));
                Ray ray = new Ray(rayOrigin, rayDir);

                Hit closestHit = Renderer.getClosestHit(ray, world);

                if (closestHit != null && closestHit.getBody() != light)
                    raysHit++;
            }
        }

        if (raysHit == 0)
            return 1;
        else
            return (1 + ((float) -raysHit / totalRays));
    }

    /**
     * Recursively bounce ray in the given world and compute colors according to the
     * reflectivities of the hit objects until the recursion limit is reached
     *
     * @param hit            the hit event
     * @param world          the world
     * @param recursionLimit the limit of how many times the ray is bounced
     * @return the resulting color of the reflections
     */
    public static Color getReflection(Hit hit, World world, int recursionLimit) {
        Vector3f hitPos = hit.getPosition();
        Vector3f rayDir = hit.getRay().getDirection();
        Vector3f reflectionDir = rayDir.subtract(hit.getNormal().multiply(2 * rayDir.dotProduct(hit.getNormal())));
        Vector3f reflectionOrigin = hitPos.add(reflectionDir.multiply(0.001F));
        float reflectivity = hit.getBody().getReflectivity() / MAX_REFLECTIVITY;

        Ray reflectionRay = new Ray(reflectionOrigin, reflectionDir);
        Hit closestHit = Renderer.getClosestHit(reflectionRay, world);

        if (closestHit != null) {
            Color finalColor;
            finalColor = getPhong(closestHit, world).multiply(reflectivity);

            if (recursionLimit != 0)
                finalColor = finalColor.add(getReflection(closestHit, world, recursionLimit - 1));

            return finalColor;
        } else
            return world.getBackgroundColor().multiply(reflectivity);
    }
}
