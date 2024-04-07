package org.iso53;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class App {
    private JPanel jpnl_backPanel;

    public App() {
        JFrame frame = new JFrame("Interactive Image Panel");
        frame.setContentPane(jpnl_backPanel);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        InteractiveImagePanel interactiveImagePanel = new InteractiveImagePanel();
        interactiveImagePanel.setImage(readDummyImage());
        interactiveImagePanel.setScalingAlgorithm(8);
        interactiveImagePanel.addZoomCapability();
        interactiveImagePanel.addMoveCapability();

        jpnl_backPanel.add(interactiveImagePanel, BorderLayout.CENTER);
    }

    public BufferedImage readDummyImage() {
        try {
            return ImageIO.read(getClass().getResourceAsStream("/dummy.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
