package org.iso53;

/**
 * Enum representing the fitting strategy for the image within the panel.
 * <br>CONTAIN: The image is resized to fit within the panel while maintaining its aspect ratio.
 * <br>COVER: The image is resized to cover the entire panel while maintaining its aspect ratio.
 * <br>ORIGINAL: The image is displayed at its original size.
 */
public enum ImageFit {
    CONTAIN, COVER, ORIGINAL
}
