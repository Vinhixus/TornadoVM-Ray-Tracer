package com.vinhphamvan.raytracer;

import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Emitter {

    private final double x;
    private final double y;
    private final List<Particle> particles;

    public Emitter(double x, double y) {
        this.x = x;
        this.y = y;
        this.particles = new ArrayList<>();
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void emit() {
        int numParticles = 3;
        for (int i = 0; i < numParticles; i++) {
            Particle p = new Particle(x, y, new Point2D((Math.random() - 0.5) * 0.65, Math.random() * -2), 10, 2.0, Color.rgb(215, 20, 65), BlendMode.ADD);
            particles.add(p);
        }
    }

    public int getParticleCount() {
        return particles.size();
    }
}
