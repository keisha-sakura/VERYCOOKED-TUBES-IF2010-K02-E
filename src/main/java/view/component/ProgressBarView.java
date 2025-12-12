package view.component;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ProgressBarView {
    private StackPane node;
    private Rectangle background;
    private Rectangle fill;

    private long startTime;
    private long duration;
    private AnimationTimer timer;
    private boolean complete = false;

    public ProgressBarView(double x, double y, double width, double height, long durationMs) {
        this.duration = durationMs;
        this.startTime = System.currentTimeMillis();

        background = new Rectangle(width, height);
        background.setFill(Color.web("#2c3e50"));
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(1);

        fill = new Rectangle(0, height);
        fill.setFill(Color.YELLOW);

        node = new StackPane(background, fill);
        node.setLayoutX(x);
        node.setLayoutY(y);

        startAnimation();
    }

    private void startAnimation() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = Math.min(1.0, (double) elapsed / duration);

                fill.setWidth(background.getWidth() * progress);
                fill.setFill(getProgressColor(progress));

                if (progress >= 1.0) {
                    complete = true;
                    stop();
                }
            }
        };
        timer.start();
    }

    private Color getProgressColor(double progress) {
        if (progress < 0.5) return Color.YELLOW;
        if (progress < 0.75) return Color.ORANGE;
        if (progress < 0.9) return Color.RED;
        return Color.DARKRED;
    }

    public boolean isComplete() {
        return complete;
    }

    public StackPane getNode() {
        return node;
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }
}
