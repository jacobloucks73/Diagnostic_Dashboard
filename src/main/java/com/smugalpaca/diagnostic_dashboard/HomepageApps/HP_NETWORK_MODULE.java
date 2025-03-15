package com.smugalpaca.diagnostic_dashboard.HomepageApps;

import oshi.SystemInfo;
import oshi.hardware.NetworkIF;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HP_NETWORK_MODULE extends JPanel {
    private SystemInfo systemInfo;
    private JPanel centerPanel;

    public HP_NETWORK_MODULE(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        initializeUI();
        updateMetrics();
    }

    // Initialize the panel layout and title
    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Title at the top
        JLabel title = new JLabel("Network Info", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Center panel for the network information
        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        add(centerPanel, BorderLayout.CENTER);
    }

    // Update the network metrics, displaying only active interfaces
    public void updateMetrics() {
        centerPanel.removeAll();

        // Retrieve the network interfaces
        List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();
        boolean activeInterfaceFound = false;

        if (networkIFs == null || networkIFs.isEmpty()) {
            centerPanel.add(new JLabel("No network information available."));
        } else {
            // Iterate over each interface and display only if bytesSent > 0 (or bytesRecv > 0)
            for (NetworkIF net : networkIFs) {
                net.updateAttributes();

                // Skip interfaces with no traffic (both bytes sent and received are 0)
                if (net.getBytesSent() == 0 && net.getBytesRecv() == 0) {
                    continue;
                }

                activeInterfaceFound = true;

                // Show a primary IPv4 address if available
                String primaryIPv4 = (net.getIPv4addr() != null && net.getIPv4addr().length > 0)
                        ? net.getIPv4addr()[0] : "N/A";

                // Create a label with essential info
                JLabel infoLabel = new JLabel(
                        "<html>" +
                                "<b>" + net.getDisplayName() + "</b><br>" +
                                "IPv4: " + primaryIPv4 + "<br>" +
                                "Sent: " + net.getBytesSent() + " bytes<br>" +
                                "Received: " + net.getBytesRecv() + " bytes" +
                                "</html>"
                );
                infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                centerPanel.add(infoLabel);
                centerPanel.add(Box.createVerticalStrut(5)); // spacing between interfaces
            }

            // If no active interfaces found, display a placeholder message
            if (!activeInterfaceFound) {
                centerPanel.add(new JLabel("No network activity yet."));
            }
        }

        revalidate();
        repaint();
    }
}
