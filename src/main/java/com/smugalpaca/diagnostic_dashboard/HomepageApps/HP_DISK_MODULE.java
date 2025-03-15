package com.smugalpaca.diagnostic_dashboard.HomepageApps;

import javax.swing.*;
import java.awt.*;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import java.util.List;

public class HP_DISK_MODULE extends JPanel {
    private SystemInfo systemInfo;

    public HP_DISK_MODULE(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        initializeUI();
        updateMetrics();
    }

    // Set up the panel's layout and static components
    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Title label at the top
        JLabel title = new JLabel("Disk Metrics", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);
    }

    // Retrieve disk metrics and update the display
    public void updateMetrics() {
        // Remove any existing center content to refresh
        Component centerComponent = getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) {
            remove(centerComponent);
        }

        // Get list of disk stores
        List<HWDiskStore> diskStores = systemInfo.getHardware().getDiskStores();

        // Create a panel to hold disk information
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        if (diskStores.isEmpty()) {
            centerPanel.add(new JLabel("No disk information available."));
        } else {
            // Iterate through each disk and add its details
            for (HWDiskStore disk : diskStores) {
                JPanel diskPanel = new JPanel(new GridLayout(0, 1));
                diskPanel.setBorder(BorderFactory.createTitledBorder(disk.getName()));

                diskPanel.add(new JLabel("Model: " + disk.getModel()));
                diskPanel.add(new JLabel("Size: " + disk.getSize() + " bytes"));
                diskPanel.add(new JLabel("Reads: " + disk.getReads()));
                diskPanel.add(new JLabel("Writes: " + disk.getWrites()));
                diskPanel.add(new JLabel("Transfer Time: " + disk.getTransferTime() + " ms"));

                centerPanel.add(diskPanel);
                centerPanel.add(Box.createVerticalStrut(5)); // spacing between disks
            }
        }

        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // Helper to retrieve a component by its layout position
    private Component getLayoutComponent(String position) {
        LayoutManager layout = getLayout();
        if (layout instanceof BorderLayout) {
            return ((BorderLayout) layout).getLayoutComponent(this, position);
        }
        return null;
    }
}
