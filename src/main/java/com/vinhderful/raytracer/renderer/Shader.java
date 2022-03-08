package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.bodies.Body;
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
     * Constant values to tweak lighting and shadows
     */
    public static final float AMBIENT_STRENGTH = 0.15F;
    public static final float SPECULAR_STRENGTH = 0.5F;
    public static final float MAX_REFLECTIVITY = 64F;
    public static final float SHADOW_STRENGTH = 0.25F;

    /**
     * PHI value for shadow sampling with sunflower seed arrangement
     */
    public static final float PHI = (float) (Math.PI * (3 - Math.sqrt(5)));

    /**
     * Get the diffuse brightness of a body given a hit event and the world
     *
     * @param hit   the hit event
     * @param world the world
     * @return the diffuse brightness
     */
    public static float getDiffuse(Hit hit, World world) {
        Light light = world.getLight();
        return Math.max(0F, hit.getNormal().dotProduct(light.getPosition().subtract(hit.getPosition()).normalize()));
    }

    /**
     * Get the specular brightness of a body given a hit event and the world
     *
     * @param hit   the hit event
     * @param world the world
     * @return the specular brightness
     */
    public static float getSpecular(Hit hit, World world) {
        Light light = world.getLight();
        Vector3f hitPos = hit.getPosition();
        Vector3f rayDirection = hit.getRay().getDirection();
        Vector3f lightDirection = light.getPosition().subtract(hitPos).normalize();
        Vector3f reflectionVector = lightDirection.subtract(hit.getNormal().multiply(2 * lightDirection.dotProduct(hit.getNormal())));

        float specularFactor = Math.max(0F, reflectionVector.dotProduct(rayDirection));
        float specularBrightness = (float) Math.pow(specularFactor, hit.getBody().getReflectivity());
        return specularBrightness * SPECULAR_STRENGTH;
    }

    /**
     * Get the factor that defines if a spot should be in shadow
     * Light is sampled using the sunflower seed arrangement
     *
     * @param hit   the hit event
     * @param world the world
     * @return the shadow factor
     */
    public static float getShadowFactor(Hit hit, World world, int sampleSize) {

        Light light = world.getLight();
        float lightScale = light.getScale();
        Vector3f lightPos = light.getPosition();
        Vector3f hitPos = hit.getPosition();

        Vector3f n = hitPos.subtract(lightPos).normalize();
        Vector3f u = n.perpVector();
        Vector3f v = n.crossProduct(u);

        int raysHit = 0;

        for (int i = 0; i < sampleSize; i++) {

            float t = PHI * i;
            float r = (float) Math.sqrt((float) i / sampleSize);

            float x = (float) (2 * lightScale * r * Math.cos(t));
            float y = (float) (2 * lightScale * r * Math.sin(t));

            Vector3f samplePoint = lightPos.add(u.multiply(x)).add(v.multiply(y));
            Vector3f rayDir = samplePoint.subtract(hitPos).normalize();
            Vector3f rayOrigin = hitPos.add(rayDir.multiply(0.001F));
            Ray sampleRay = new Ray(rayOrigin, rayDir);

            Hit sampleHit = Renderer.getClosestHit(sampleRay, world);
            if (sampleHit != null && !sampleHit.getBody().equals(light))
                raysHit++;
        }

        if (raysHit == 0) return 1;
        else return 1 - (float) raysHit / (sampleSize * (1 + SHADOW_STRENGTH));
    }

    /**
     * Recursively bounce ray in the given world and compute colors according to the
     * reflectivities of the hit objects until the recursion limit is reached
     *
     * @param hit                   the hit event
     * @param world                 the world
     * @param shadowSampleSize      number of samples for soft shadow sampling
     * @param reflectionBounceLimit the limit of how many times the ray is bounced
     * @return the resulting final pixel color
     */
    public static Color getPixelColor(Hit hit, World world, int shadowSampleSize, int reflectionBounceLimit) {

        Vector3f hitPos = hit.getPosition();
        Vector3f rayDir = hit.getRay().getDirection();
        Body hitBody = hit.getBody();

        Color hitColor = hitBody.getColor(hitPos);

        float diffuse = Math.max(AMBIENT_STRENGTH, getDiffuse(hit, world));
        float specular = getSpecular(hit, world);
        float reflectivity = hitBody.getReflectivity() / MAX_REFLECTIVITY;
        float shadow = getShadowFactor(hit, world, shadowSampleSize);

        Color reflection;
        Vector3f reflectionDir = rayDir.subtract(hit.getNormal().multiply(2 * rayDir.dotProduct(hit.getNormal())));
        Vector3f reflectionOrigin = hitPos.add(reflectionDir.multiply(0.001F));
        Hit reflectionHit = reflectionBounceLimit > 0 ? Renderer.getClosestHit(new Ray(reflectionOrigin, reflectionDir), world) : null;

        if (reflectionHit != null)
            reflection = getPixelColor(reflectionHit, world, shadowSampleSize, reflectionBounceLimit - 1);
        else
            reflection = world.getSkybox().getColor(reflectionDir);

        if (hitBody.equals(world.getPlane()))
            return hitColor.mix(reflection, reflectivity).add(specular).multiply(shadow);
        else
            return hitColor.mix(reflection, reflectivity).multiply(diffuse).add(specular).multiply(shadow);
    }
}
