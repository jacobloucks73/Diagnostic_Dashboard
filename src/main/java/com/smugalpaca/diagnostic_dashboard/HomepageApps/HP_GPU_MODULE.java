package com.smugalpaca.diagnostic_dashboard.HomepageApps;

import javax.swing.*;
import java.awt.*;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import java.util.List;

public class HP_GPU_MODULE extends JPanel {
    private SystemInfo systemInfo;
    private JPanel centerPanel;

    public HP_GPU_MODULE(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        initializeUI();
        updateMetrics();
    }

    // Set up the UI layout and static components
    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Title at the top of the panel
        JLabel title = new JLabel("GPU Metrics", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Center panel to list GPU details
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        add(centerPanel, BorderLayout.CENTER);
    }

    // Retrieve GPU details and update the display
    public void updateMetrics() {
        // Clear previous content
        centerPanel.removeAll();

        // Retrieve graphics card list from OSHI
        List<GraphicsCard> graphicsCards = systemInfo.getHardware().getGraphicsCards();

        if (graphicsCards == null || graphicsCards.isEmpty()) {
            centerPanel.add(new JLabel("No GPU information available."));
        } else {
            // Iterate through each graphics card and display its details
            for (GraphicsCard card : graphicsCards) {
                JPanel cardPanel = new JPanel(new GridLayout(0, 1));
                cardPanel.setBorder(BorderFactory.createTitledBorder(card.getName()));

                cardPanel.add(new JLabel("Vendor: " + card.getVendor()));
                cardPanel.add(new JLabel("VRAM: " + card.getVRam() + " bytes"));
                cardPanel.add(new JLabel("Device ID: " + card.getDeviceId()));
                cardPanel.add(new JLabel("Version Info: " + card.getVersionInfo()));

                centerPanel.add(cardPanel);
                centerPanel.add(Box.createVerticalStrut(5)); // spacing between cards
            }
        }

        // Refresh the panel UI
        revalidate();
        repaint();
    }
}
