package com.smugalpaca.diagnostic_dashboard.HomepageApps;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class HP_RAM_MODULE extends JPanel {
    private SystemInfo systemInfo;
    private GlobalMemory memory;
    private OperatingSystem os;
    private JLabel ramUsageLabel;
    private ChartPanel pieChartPanel;
    private JPanel processDetailsPanel; // Panel for process breakdown details below the pie chart

    public HP_RAM_MODULE(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        this.memory = systemInfo.getHardware().getMemory();
        this.os = systemInfo.getOperatingSystem();
        initializeUI();
        updateMetrics();
    }

    private void initializeUI() {
        // Use BorderLayout for the module
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Title label at the top of the module
        JLabel title = new JLabel("RAM Metrics", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Create a center panel to hold the overall RAM usage label and the pie chart vertically
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Overall RAM usage label (placed at the top of the center panel)
        ramUsageLabel = new JLabel("RAM Usage: Calculating...", SwingConstants.CENTER);
        ramUsageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        ramUsageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(ramUsageLabel);
        centerPanel.add(Box.createVerticalStrut(10)); // spacing

        // Create an initial pie chart with an empty dataset
        DefaultPieDataset dataset = new DefaultPieDataset();
        JFreeChart chart = ChartFactory.createPieChart(
                "Top 10 Memory Processes",
                dataset,
                true,   // include legend
                true,
                false
        );
        pieChartPanel = new ChartPanel(chart);
        pieChartPanel.setPreferredSize(new Dimension(300, 200));
        centerPanel.add(pieChartPanel);

        // Add the center panel to the main module in the center region
        add(centerPanel, BorderLayout.CENTER);

        // South panel for detailed process breakdown
        processDetailsPanel = new JPanel();
        processDetailsPanel.setLayout(new BoxLayout(processDetailsPanel, BoxLayout.Y_AXIS));
        processDetailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(processDetailsPanel, BorderLayout.SOUTH);
    }

    public void updateMetrics() {
        // --- Update overall RAM usage ---
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        double usedPercent = usedMemory * 100.0 / totalMemory;

        // Convert values to MB for display
        long totalMB = totalMemory / (1024 * 1024);
        long usedMB = usedMemory / (1024 * 1024);
        ramUsageLabel.setText(String.format("RAM Usage: %d MB / %d MB (%.2f%%)", usedMB, totalMB, usedPercent));

        // --- Update pie chart for process memory usage ---
        // Retrieve processes (using all available processes)
        List<OSProcess> processes = os.getProcesses();
        // Sort processes in descending order of memory usage (resident set size)
        processes.sort(Comparator.comparingLong(OSProcess::getResidentSetSize).reversed());

        DefaultPieDataset dataset = new DefaultPieDataset();
        double othersTotal = 0.0;
        int topCount = 10;

        // Clear the process details panel for updating
        processDetailsPanel.removeAll();
        processDetailsPanel.add(new JLabel("Process Memory Breakdown:"));

        // Iterate over sorted processes to build dataset and add details
        for (int i = 0; i < processes.size(); i++) {
            OSProcess proc = processes.get(i);
            // Convert resident set size to MB
            double procMemoryMB = proc.getResidentSetSize() / (1024.0 * 1024.0);
            if (i < topCount) {
                dataset.setValue(proc.getName(), procMemoryMB);
                // Add label for this process
                processDetailsPanel.add(new JLabel(String.format("%s: %.2f MB", proc.getName(), procMemoryMB)));
            } else {
                othersTotal += procMemoryMB;
            }
        }
        if (othersTotal > 0) {
            dataset.setValue("Others", othersTotal);
            processDetailsPanel.add(new JLabel(String.format("Others: %.2f MB", othersTotal)));
        }

        // Optionally, add a summary total for the top processes
        double topTotal = 0.0;
        for (int i = 0; i < Math.min(topCount, processes.size()); i++) {
            topTotal += processes.get(i).getResidentSetSize();
        }
        topTotal = topTotal / (1024.0 * 1024.0); // in MB
        processDetailsPanel.add(Box.createVerticalStrut(5));
        processDetailsPanel.add(new JLabel(String.format("Top %d Total: %.2f MB", topCount, topTotal)));

        // Update the pie chart with the new dataset
        JFreeChart chart = ChartFactory.createPieChart(
                "Top 10 Memory Processes",
                dataset,
                true,
                true,
                false
        );
        pieChartPanel.setChart(chart);

        // Refresh the module UI
        revalidate();
        repaint();
    }

    // For testing this module independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SystemInfo si = new SystemInfo();
            JFrame frame = new JFrame("RAM Module with Pie Chart and Process Details");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            HP_RAM_MODULE ramModule = new HP_RAM_MODULE(si);
            frame.add(ramModule);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
