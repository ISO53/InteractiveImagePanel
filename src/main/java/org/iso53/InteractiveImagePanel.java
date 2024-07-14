package org.iso53;

import org.imgscalr.Scalr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
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
     * The initial fitting strategy for the image within the panel. This value determines how the image is initially
     * displayed when the panel is created.
     */
    private ImageFit imageFit;

    /**
     * The scaling strategy used for image processing within the panel.
     * This field determines how images are scaled when the panel's scaling functionality is invoked.
     * The scaling strategy can be either FAST, which prioritizes speed over quality, or PRETTY,
     * which prioritizes image quality over speed. This allows for flexible image scaling
     * based on the requirements of the application.
     */
    private Scale scale;

    /**
     * Constructs a new InteractiveImagePanel with default settings.
     */
    public InteractiveImagePanel() {
        this.maxZoomFactor = 2.5;
        this.minZoomFactor = 0.25;
        this.zoomStep = 0.025;
        this.zoom = 1.0;
        this.lastPressed = new Point(0, 0);
        this.currPosition = new Point(0, 0);
        this.tempPosition = new Point(0, 0);
        this.imageFit = ImageFit.COVER;
        this.scale = Scale.FAST;

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
     * @param maxZoomFactor the maximum zoom factor for the image.
     * @param minZoomFactor the minimum zoom factor for the image.
     * @param zoomStep      the amount of zoom to be applied on each scroll with mouse wheel.
     * @param image         the image to be displayed on the panel.
     * @param imageFit      fitting strategy for the image.
     * @param scale         the scaling strategy to use.
     */
    public InteractiveImagePanel(double maxZoomFactor, double minZoomFactor, double zoomStep, BufferedImage image,
                                 ImageFit imageFit, Scale scale) {
        this.maxZoomFactor = maxZoomFactor;
        this.minZoomFactor = minZoomFactor;
        this.zoomStep = zoomStep;
        this.image = image;
        this.lastPressed = new Point(0, 0);
        this.currPosition = new Point(0, 0);
        this.tempPosition = new Point(0, 0);
        this.imageFit = imageFit;
        this.scale = scale;

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
            g.drawImage(scaleImage(image, zoom), currPosition.x, currPosition.y, this);
        }
    }

    /**
     * Scales the given image by the specified ratio using the AffineTransform method.
     *
     * @param source the original BufferedImage to be scaled.
     * @param ratio  the scaling ratio. A ratio greater than 1 enlarges the image, less than 1 shrinks the image.
     * @return a new BufferedImage that is a scaled version of the original image.
     */
    private BufferedImage scaleImageFast(BufferedImage source, double ratio) {
        BufferedImage bi = new BufferedImage(
                (int) (source.getWidth() * ratio),
                (int) (source.getHeight() * ratio),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(ratio, ratio);
        g2d.drawRenderedImage(source, at);
        g2d.dispose();
        return bi;
    }

    /**
     * Scales the given image to the specified ratio with a focus on maintaining high quality.
     * This method uses the imgscalr library's Scalr.resize method to perform the scaling operation.
     * The scaling process prioritizes image quality over scaling speed, making it suitable for applications
     * where the visual quality of the scaled image is paramount. The method automatically determines
     * the scaling mode (e.g., fit to width, fit to height) based on the target dimensions.
     *
     * @param source the original BufferedImage to be scaled.
     * @param ratio  the scaling ratio. A ratio greater than 1 enlarges the image, less than 1 shrinks the image.
     * @return a new BufferedImage that is a scaled version of the original image.
     */
    private BufferedImage scaleImagePretty(BufferedImage source, double ratio) {
        int targetWidth = (int) (source.getWidth() * ratio);
        int targetHeight = (int) (source.getHeight() * ratio);

        return Scalr.resize(source, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight);
    }

    /**
     * Scales the given image according to the specified ratio and scaling strategy.
     * This method provides a flexible way to scale images either quickly or with high quality,
     * depending on the scaling strategy specified. The method supports two strategies: FAST and PRETTY.
     * FAST uses an AffineTransform for quicker scaling but might compromise on quality.
     * PRETTY uses the imgscalr library's Scalr.resize method for higher quality scaling at the cost of speed.
     * If an unsupported scaling strategy is specified, the method prints an error message and returns null.
     *
     * @param source the original BufferedImage to be scaled.
     * @param ratio  the scaling ratio. A ratio greater than 1 enlarges the image, less than 1 shrinks the image.
     * @return a new BufferedImage that is a scaled version of the original image or null if an unsupported scaling
     * strategy is specified.
     */
    private BufferedImage scaleImage(BufferedImage source, double ratio) {
        switch (scale) {
            case FAST -> {
                return scaleImageFast(source, ratio);
            }
            case PRETTY -> {
                return scaleImagePretty(source, ratio);
            }
            default -> {
                System.out.println("Impossible");
                return null;
            }
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
     * Sets the initial fitting strategy for the image within the panel.
     *
     * @param imageFit an ImageFit enum value representing the initial fitting strategy.
     * @see ImageFit
     */
    public void setImageFit(ImageFit imageFit) {
        this.imageFit = imageFit;
    }

    /**
     * Sets the scaling strategy for the image.
     *
     * @param scale the scaling strategy to use, represented by the Scale enum.
     */
    public void setScale(Scale scale) {
        this.scale = scale;
    }

    /**
     * Adjusts the zoom level and position of the image based on the current image fit strategy. After adjusting the
     * zoom level and position, the panel is refreshed to reflect the changes.
     *
     * @see ImageFit
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
