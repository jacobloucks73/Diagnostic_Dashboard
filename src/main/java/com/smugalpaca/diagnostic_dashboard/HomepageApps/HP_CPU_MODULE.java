package com.smugalpaca.diagnostic_dashboard.HomepageApps;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
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

public class HP_CPU_MODULE extends JPanel {
    private SystemInfo systemInfo;
    private CentralProcessor processor;
    private OperatingSystem os;
    private JLabel cpuUsageLabel;
    private ChartPanel pieChartPanel;
    private JPanel processDetailsPanel; // Panel for process details below the pie chart

    public HP_CPU_MODULE(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        this.processor = systemInfo.getHardware().getProcessor();
        this.os = systemInfo.getOperatingSystem();
        initializeUI();
        updateMetrics();
    }

    private void initializeUI() {
        // Use BorderLayout for the module
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Title label at the top of the module
        JLabel title = new JLabel("CPU Metrics", SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // Create a center panel to hold the CPU load label and the pie chart vertically
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // CPU load label (placed at the top of the center panel)
        cpuUsageLabel = new JLabel("CPU Load: Calculating...", SwingConstants.CENTER);
        cpuUsageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        cpuUsageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(cpuUsageLabel);
        centerPanel.add(Box.createVerticalStrut(10)); // spacing

        // Create an initial pie chart with an empty dataset
        DefaultPieDataset dataset = new DefaultPieDataset();
        JFreeChart chart = ChartFactory.createPieChart(
                "Top 10 CPU Processes",
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

        // South panel for detailed process percentages
        processDetailsPanel = new JPanel();
        processDetailsPanel.setLayout(new BoxLayout(processDetailsPanel, BoxLayout.Y_AXIS));
        processDetailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(processDetailsPanel, BorderLayout.SOUTH);
    }

    public void updateMetrics() {
        // --- Update overall CPU load ---
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(1000);  // Pause for measurement (consider using a Swing Timer to avoid blocking EDT)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        cpuUsageLabel.setText(String.format("CPU Load: %.2f%%", cpuLoad));

        // --- Update pie chart for process CPU usage ---
        // Retrieve processes (using all available processes)
        List<OSProcess> processes = os.getProcesses();
        // Sort processes in descending order of cumulative CPU load
        processes.sort(Comparator.comparingDouble(OSProcess::getProcessCpuLoadCumulative).reversed());

        DefaultPieDataset dataset = new DefaultPieDataset();
        double othersTotal = 0.0;
        int topCount = 10;

        // Clear the process details panel for updating
        processDetailsPanel.removeAll();
        processDetailsPanel.add(new JLabel("Process Breakdown:"));

        // Iterate over sorted processes to build dataset and add details
        for (int i = 0; i < processes.size(); i++) {
            OSProcess proc = processes.get(i);
            double procCpu = proc.getProcessCpuLoadCumulative() * 10;  // FIX NUMBERS NOT LINING UP
            if (i < topCount) {
                dataset.setValue(proc.getName(), procCpu);
                // Add label for this process
                processDetailsPanel.add(new JLabel(String.format("%s: %.2f%%", proc.getName(), procCpu)));
            } else {
                othersTotal += procCpu;
            }
        }
        if (othersTotal > 0) {
            dataset.setValue("Others", othersTotal);
            processDetailsPanel.add(new JLabel(String.format("Others: %.2f%%", othersTotal)));
        }

        // Optionally add a total of the top processes
        double topTotal = 100 - othersTotal; // This is a rough estimate assuming percentages add to 100%
        processDetailsPanel.add(Box.createVerticalStrut(5));
        processDetailsPanel.add(new JLabel(String.format("Top %d Total: %.2f%%", topCount, topTotal)));

        // Update the pie chart with the new dataset
        JFreeChart chart = ChartFactory.createPieChart(
                "Top 10 CPU Processes",
                dataset,
                true,
                true,
                false
        );
        pieChartPanel.setChart(chart);

        // Refresh the module
        revalidate();
        repaint();
    }

    // For testing this module independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SystemInfo si = new SystemInfo();
            JFrame frame = new JFrame("CPU Module with Pie Chart and Process Details");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            HP_CPU_MODULE cpuModule = new HP_CPU_MODULE(si);
            frame.add(cpuModule);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
