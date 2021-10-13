package com.vinhderful.raytracer.renderer;

import com.vinhderful.raytracer.utils.Color;
import com.vinhderful.raytracer.utils.Vector3f;

public class Shader {

    public static final float AMBIENT_STRENGTH = 0.05F;
    public static final float SPECULAR_STRENGTH = 0.5F;

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

    public static Color getSpecular(Hit hit, World world) {
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
