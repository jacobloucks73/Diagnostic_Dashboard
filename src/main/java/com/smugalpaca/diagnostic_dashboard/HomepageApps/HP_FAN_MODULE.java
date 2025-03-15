package com.smugalpaca.diagnostic_dashboard.HomepageApps;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import oshi.SystemInfo;
import oshi.hardware.Sensors;

public class HP_FAN_MODULE extends JPanel {
    private SystemInfo systemInfo;
    private Sensors sensors;
    private JPanel centerPanel;

    public HP_FAN_MODULE(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        this.sensors = systemInfo.getHardware().getSensors();
        initializeUI();
        updateMetrics();
    }

    // Initialize the UI components
    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Title at the top
        JLabel title = new JLabel("Fan Metrics", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH); 

        // Center panel to display fan speeds
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        add(centerPanel, BorderLayout.CENTER);
    }

    // Update the panel with the latest fan metrics
    public void updateMetrics() {
        // Clear previous components
        centerPanel.removeAll();

        // Get fan speeds from sensors
        int[] fanSpeeds = sensors.getFanSpeeds();
        //debug
        //System.out.println("Fan speeds: " + Arrays.toString(fanSpeeds));


        if (fanSpeeds == null || fanSpeeds.length == 0) {
            centerPanel.add(new JLabel("No fan data available."));
        } else {
            // Loop through each fan and display its speed
            for (int i = 0; i < fanSpeeds.length; i++) {
                JLabel fanLabel = new JLabel("Fan " + (i + 1) + " Speed: " + fanSpeeds[i] + " RPM");
                centerPanel.add(fanLabel);
                centerPanel.add(Box.createVerticalStrut(5)); // spacing between fans
            }
        }

        // Refresh the panel
        revalidate();
        repaint();
    }
}
