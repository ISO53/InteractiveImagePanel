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
        this.maxZoomFactor = 2.5;
        this.minZoomFactor = 0.25;
        this.zoomStep = 0.025;
        this.zoom = 1.0;
        this.scalingAlgorithm = Image.SCALE_DEFAULT;
        this.lastPressed = new Point(0, 0);
        this.currPosition = new Point(0, 0);
        this.tempPosition = new Point(0, 0);
    public InteractiveImagePanel(double maxZoomFactor, double minZoomFactor, double zoomStep, BufferedImage image,
                                 int scalingAlgorithm) {
        this.maxZoomFactor = maxZoomFactor;
        this.minZoomFactor = minZoomFactor;
        this.zoomStep = zoomStep;
        this.image = image;
        this.scalingAlgorithm = scalingAlgorithm;
        this.lastPressed = new Point(0, 0);
        this.currPosition = new Point(0, 0);
        this.tempPosition = new Point(0, 0);
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
                    currPosition.x,
                    currPosition.y,
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
    private void refresh() {
        revalidate();
        repaint();
    }
    public void setMaxZoomFactor(double maxZoomFactor) {
        this.maxZoomFactor = maxZoomFactor;
    }
    public void setMinZoomFactor(double minZoomFactor) {
        this.minZoomFactor = minZoomFactor;
    }
    public void setZoomStep(double zoomStep) {
        this.zoomStep = zoomStep;
    }
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setScalingAlgorithm(int scaleAlgorithm) throws IllegalArgumentException {
        if (scaleAlgorithm == Image.SCALE_DEFAULT || scaleAlgorithm == Image.SCALE_FAST || scaleAlgorithm == Image.SCALE_REPLICATE || scaleAlgorithm == Image.SCALE_SMOOTH || scaleAlgorithm == Image.SCALE_AREA_AVERAGING) {
            this.scalingAlgorithm = scaleAlgorithm;
        } else {
            throw new IllegalArgumentException("Invalid scaling algorithm");
        }
    }
}
