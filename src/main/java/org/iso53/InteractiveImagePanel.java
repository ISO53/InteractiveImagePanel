package org.iso53;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class InteractiveImagePanel extends JPanel {

    private double zoom;
    private BufferedImage image;
    private int scalingAlgorithm;

    public InteractiveImagePanel() {
        this.addMouseWheelListener(e -> {
            if (e.getPreciseWheelRotation() < 0) {
                if (zoom < 2.5) {
                    zoom += 0.025;
                }
            } else {
                if (zoom > 0.25) {
                    zoom -= 0.025;
                }
            }
            revalidate();
            repaint();
        });
        this.zoom = 1.0;
        this.scalingAlgorithm = Image.SCALE_DEFAULT;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            g.drawImage(
                    image.getScaledInstance(
                            (int) (image.getWidth() * zoom),
                            (int) (image.getHeight() * zoom),
                            scalingAlgorithm),
                    0,
                    0,
                    this);
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setScaleAlgorithm(int scaleAlgorithm) throws IllegalArgumentException {
        if (scaleAlgorithm == Image.SCALE_DEFAULT ||
                scaleAlgorithm == Image.SCALE_FAST ||
                scaleAlgorithm == Image.SCALE_REPLICATE ||
                scaleAlgorithm == Image.SCALE_SMOOTH ||
                scaleAlgorithm == Image.SCALE_AREA_AVERAGING) {
            this.scalingAlgorithm = scaleAlgorithm;
        } else {
            throw new IllegalArgumentException("Invalid scale algorithm");
        }
    }
}
