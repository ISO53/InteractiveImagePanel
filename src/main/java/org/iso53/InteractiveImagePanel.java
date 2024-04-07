package org.iso53;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class InteractiveImagePanel extends JPanel {

    private double maxZoomFactor;
    private double minZoomFactor;
    private double zoomStep;
    private double zoom;
    private BufferedImage image;
    private int scalingAlgorithm;

    private final Point lastPressed;
    private final Point currPosition;
    private final Point tempPosition;
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

    public void addZoomCapability() {
        this.addMouseWheelListener(e -> {
            // Store old zoom value in a variable
            double oldZoom = zoom;

            // Adjust zoom
            if (e.getPreciseWheelRotation() < 0) {
                if (zoom < maxZoomFactor) {
                    zoom += zoomStep;
                }
            } else {
                if (zoom > minZoomFactor) {
                    zoom -= zoomStep;
                }
            }

            // Calculate the mouse position relative to the image
            Point mousePosition = e.getPoint();
            double relativeX = (mousePosition.x - currPosition.x) / oldZoom;
            double relativeY = (mousePosition.y - currPosition.y) / oldZoom;

            // Adjust the position so that the mouse position remains at the same point in the image
            currPosition.x = (int) (mousePosition.x - relativeX * zoom);
            currPosition.y = (int) (mousePosition.y - relativeY * zoom);

            refresh();
        });
    }
    public void addMoveCapability() {
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                lastPressed.setLocation(e.getPoint());
                tempPosition.setLocation(currPosition);
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                currPosition.setLocation(
                        tempPosition.x + (e.getX() - lastPressed.x),
                        tempPosition.y + (e.getY() - lastPressed.y)
                );
                refresh();
            }
        });
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
