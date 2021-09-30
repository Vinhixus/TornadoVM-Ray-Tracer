package com.vinhphamvan.raytracer;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Iterator;

public class Controller {

    @FXML
    public Pane pane;

    @FXML
    public Canvas canvas;

    @FXML
    public Label fpsLabel;

    @FXML
    public Label particleCountLabel;

    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;
    private boolean arrayFilled = false;

    private GraphicsContext g;
    private Emitter emitter = null;

    @FXML
    public void initialize() {
        canvas.setManaged(false);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        emitter = new Emitter(100, 250);

        g = canvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                fpsLabel.setText(String.format("FPS: %.2f", getFPS(now)));
                particleCountLabel.setText(String.format("Count: %d", emitter.getParticleCount()));
                onUpdate();
            }
        };
        timer.start();
    }

    private void onUpdate() {
        g.setGlobalAlpha(1.0);
        g.setGlobalBlendMode(BlendMode.SRC_OVER);
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        emitter.emit();

        for (Iterator<Particle> it = emitter.getParticles().iterator(); it.hasNext();) {
            Particle p = it.next();
            p.setX(p.getX() + p.getVelocity().getX());
            p.setY(p.getY() + p.getVelocity().getY());
            p.setLife(p.getLife() - p.getDecay());

            if (!p.isAlive()) {
                it.remove();
                continue;
            }

            g.setGlobalAlpha(p.getLife());
            g.setGlobalBlendMode(p.getBlendMode());
            g.setFill(p.getColor());
            g.fillOval(p.getX(), p.getY(), p.getRadius(), p.getRadius());
        }
    }

    private double getFPS(long now) {
        long oldFrameTime = frameTimes[frameTimeIndex];
        frameTimes[frameTimeIndex] = now;
        frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
        if (frameTimeIndex == 0)
            arrayFilled = true;

        if (arrayFilled) {
            long elapsedNanosPerFrame = (now - oldFrameTime) / frameTimes.length;
            return 1_000_000_000.0 / elapsedNanosPerFrame;
        }

        return 0;
    }

}
