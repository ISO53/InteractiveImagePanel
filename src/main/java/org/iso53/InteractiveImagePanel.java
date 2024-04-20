package org.iso53;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * This class represents an interactive image panel that can be zoomed and moved. It provides methods to set the zoom
 * level, move the image, and set the image to be displayed. The image can be zoomed in and out with the mouse wheel,
 * and moved by clicking and dragging.
 */
public class InteractiveImagePanel extends JPanel {

    /**
     * The maximum zoom factor for the image. The zoom factor determines how much the image can be zoomed in.
     */
    private double maxZoomFactor;

    /**
     * The minimum zoom factor for the image. The zoom factor determines how much the image can be zoomed out.
     */
    private double minZoomFactor;

    /**
     * The amount of zoom to be applied on each scroll with mouse wheel.
     */
    private double zoomStep;

    /**
     * The current zoom level of the image.
     */
    private double zoom;

    /**
     * The image to be displayed on the panel.
     */
    private BufferedImage image;

    /**
     * The scaling algorithm to be used for zooming the image.
     */
    private int scalingAlgorithm;

    /**
     * The last point where the mouse was pressed.
     */
    private final Point lastPressed;

    /**
     * The current position of the image on the panel.
     */
    private final Point currPosition;

    /**
     * A temporary position used for moving the image.
     */
    private final Point tempPosition;

    /**
     * Enum representing the fitting strategy for the image within the panel.
     * <br>CONTAIN: The image is resized to fit within the panel while maintaining its aspect ratio.
     * <br>COVER: The image is resized to cover the entire panel while maintaining its aspect ratio.
     * <br>ORIGINAL: The image is displayed at its original size.
     */
    public enum IMAGE_FIT {
        CONTAIN, COVER, ORIGINAL
    }

    /**
     * The initial fitting strategy for the image within the panel. This value determines how the image is initially
     * displayed when the panel is created.
     */
    private IMAGE_FIT imageFit;

    /**
     * Constructs a new InteractiveImagePanel with default settings.
     */
    public InteractiveImagePanel() {
        this.maxZoomFactor = 2.5;
        this.minZoomFactor = 0.25;
        this.zoomStep = 0.025;
        this.zoom = 1.0;
        this.scalingAlgorithm = Image.SCALE_DEFAULT;
        this.lastPressed = new Point(0, 0);
        this.currPosition = new Point(0, 0);
        this.tempPosition = new Point(0, 0);
        this.imageFit = IMAGE_FIT.COVER;

        // Add a ComponentListener to adjust the zoom and position when the component is shown
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustImageFit();
            }
        });
    }

    /**
     * Constructs a new InteractiveImagePanel with the specified settings.
     *
     * @param maxZoomFactor    the maximum zoom factor for the image.
     * @param minZoomFactor    the minimum zoom factor for the image.
     * @param zoomStep         the amount of zoom to be applied on each scroll with mouse wheel.
     * @param image            the image to be displayed on the panel.
     * @param scalingAlgorithm the scaling algorithm to be used for zooming the image.
     */
    public InteractiveImagePanel(double maxZoomFactor, double minZoomFactor, double zoomStep, BufferedImage image,
                                 int scalingAlgorithm, IMAGE_FIT imageFit) {
        this.maxZoomFactor = maxZoomFactor;
        this.minZoomFactor = minZoomFactor;
        this.zoomStep = zoomStep;
        this.image = image;
        this.scalingAlgorithm = scalingAlgorithm;
        this.lastPressed = new Point(0, 0);
        this.currPosition = new Point(0, 0);
        this.tempPosition = new Point(0, 0);
        this.imageFit = imageFit;

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustImageFit();
            }
        });
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

    /**
     * This method is used to add zoom capability to the image panel. It adds a mouse wheel listener to the panel
     * which adjusts the zoom level and the position of the image based on the rotation of the mouse wheel and the
     * position of the mouse pointer.
     */
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

    /**
     * This method is used to add move capability to the image panel. It adds a mouse listener and a mouse motion
     * listener to the panel which adjust the position of the image based on the position of the mouse pointer and
     * the dragging of the mouse.
     */
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

    /**
     * This method is used to refresh the panel. It first validates this component and all of its subcomponents, then
     * it causes the component to be repainted.
     */
    private void refresh() {
        revalidate();
        repaint();
    }

    /**
     * This method is used to set the maximum zoom factor for the image. The zoom factor determines how much the
     * image can be zoomed in.
     *
     * @param maxZoomFactor a double value representing the maximum zoom factor. Default is 2.5.
     */
    public void setMaxZoomFactor(double maxZoomFactor) {
        this.maxZoomFactor = maxZoomFactor;
    }

    /**
     * This method is used to set the minimum zoom factor for the image. The zoom factor determines how much the
     * image can be zoomed out.
     *
     * @param minZoomFactor a double value representing the minimum zoom factor. Default is 0.25.
     */
    public void setMinZoomFactor(double minZoomFactor) {
        this.minZoomFactor = minZoomFactor;
    }

    /**
     * Sets the amount of zoom to be applied on each scroll with mouse wheel. Smaller the value smooth the zoom
     * effect but also slow. Bigger the value smoothness decreases but faster to zoom-in/out.
     *
     * @param zoomStep double value for zoom step. Default is 0.025;
     */
    public void setZoomStep(double zoomStep) {
        this.zoomStep = zoomStep;
    }

    /**
     * Sets the image to be rendered.
     *
     * @param image the image for displaying on the panel.
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    /**
     * Decides which scaling algorithm should be used for scaling (zoom-in, zoom-out).
     *
     * @param scaleAlgorithm should be:<br>
     *                       1  -> Image.SCALE_DEFAULT,<br>
     *                       2  -> Image.SCALE_FAST,<br>
     *                       4  -> Image.SCALE_REPLICATE,<br>
     *                       8  -> Image.SCALE_SMOOTH<br>
     *                       16 -> Image.SCALE_AREA_AVERAGING.<br>
     * @throws IllegalArgumentException if a parameter other than the specified ones is given it will throw
     *                                  IllegalArgumentException.
     * @see Image
     */
    public void setScalingAlgorithm(int scaleAlgorithm) throws IllegalArgumentException {
        if (scaleAlgorithm == Image.SCALE_DEFAULT || scaleAlgorithm == Image.SCALE_FAST || scaleAlgorithm == Image.SCALE_REPLICATE || scaleAlgorithm == Image.SCALE_SMOOTH || scaleAlgorithm == Image.SCALE_AREA_AVERAGING) {
            this.scalingAlgorithm = scaleAlgorithm;
        } else {
            throw new IllegalArgumentException("Invalid scaling algorithm");
        }
    }

    /**
     * Sets the initial fitting strategy for the image within the panel.
     *
     * @param imageFit an IMAGE_FIT enum value representing the initial fitting strategy.
     * @see IMAGE_FIT
     */
    public void setImageFit(IMAGE_FIT imageFit) {
        this.imageFit = imageFit;
    }

    /**
     * Adjusts the zoom level and position of the image based on the current image fit strategy. After adjusting the
     * zoom level and position, the panel is refreshed to reflect the changes.
     *
     * @see IMAGE_FIT
     */
    private void adjustImageFit() {
        if (image == null) {
            return;
        }

        switch (this.imageFit) {
            case CONTAIN:
                // Adjust zoom and position for CONTAIN strategy
                zoom = Math.min((double) getWidth() / image.getWidth(), (double) getHeight() / image.getHeight());
                currPosition.x = (getWidth() - (int) (image.getWidth() * zoom)) / 2;
                currPosition.y = (getHeight() - (int) (image.getHeight() * zoom)) / 2;
                break;
            case COVER:
                // Adjust zoom and position for COVER strategy
                zoom = Math.max((double) getWidth() / image.getWidth(), (double) getHeight() / image.getHeight());
                currPosition.x = (getWidth() - (int) (image.getWidth() * zoom)) / 2;
                currPosition.y = (getHeight() - (int) (image.getHeight() * zoom)) / 2;
                break;
            case ORIGINAL:
                // Adjust zoom and position for ORIGINAL strategy
                zoom = 1.0;
                currPosition.x = (getWidth() - image.getWidth()) / 2;
                currPosition.y = (getHeight() - image.getHeight()) / 2;
                break;
        }

        refresh();
    }
}
