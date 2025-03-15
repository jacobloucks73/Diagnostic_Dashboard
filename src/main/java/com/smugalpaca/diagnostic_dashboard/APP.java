package com.smugalpaca.diagnostic_dashboard;

import com.smugalpaca.diagnostic_dashboard.HomepageApps.*;
import oshi.SystemInfo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class APP extends JFrame {
    // Module instance variables
    private HP_CPU_MODULE cpuModule;
    private HP_GPU_MODULE gpuModule;
    private HP_RAM_MODULE ramModule;
    private HP_DISK_MODULE diskModule;
    private HP_FAN_MODULE fanModule;
    private HP_NETWORK_MODULE networkModule;

    public APP() {
        SystemInfo si = new SystemInfo();
        setTitle("System Monitoring Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AtomicBoolean running = new AtomicBoolean(true);
        setLayout(new BorderLayout(10, 10)); // adds horizontal & vertical gaps

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createLineBorder(Color.GRAY));


        // Main panel with 6 evenly spaced boxes (2 rows x 3 columns)
        JPanel mainPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        for (int i = 1; i <= 6; i++) {
            JPanel contentBox = new JPanel();
            contentBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            switch (i) {
                case 1:
                    cpuModule = new HP_CPU_MODULE(si);
                    contentBox.add(cpuModule);
                    break;
                case 2:
                    gpuModule = new HP_GPU_MODULE(si);
                    contentBox.add(gpuModule);
                    break;
                case 3:
                    ramModule = new HP_RAM_MODULE(si);
                    contentBox.add(ramModule);
                    break;
                case 4:
                    diskModule = new HP_DISK_MODULE(si);
                    contentBox.add(diskModule);
                    break;
                case 5:
                    fanModule = new HP_FAN_MODULE(si);
                    contentBox.add(fanModule);
                    break;
                case 6:
                    networkModule = new HP_NETWORK_MODULE(si);
                    contentBox.add(networkModule);
                    break;
            }
            mainPanel.add(contentBox);
        }

        // Create and add a few buttons to the sidebar
        for (int i = 1; i <= 7; i++) {

            switch(i) {
                case 1:
                    JButton Cbutton = new JButton("CPU MODULE");
                    Cbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    //Cbutton.setSize(30,75);
                    sidebar.add(Cbutton);
                    break;

                case 2:
                    JButton Gbutton = new JButton("GPU MODULE");
                    Gbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    //Gbutton.setSize(30,75);
                    sidebar.add(Gbutton);
                    break;

                case 3:
                    JButton Rbutton = new JButton("RAM MODULE");
                    Rbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    //Rbutton.setSize(30,75);
                    sidebar.add(Rbutton);
                    break;

                case 4:
                    JButton Dbutton = new JButton("DISK MODULE");
                    Dbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    //Dbutton.setSize(30,75);
                    sidebar.add(Dbutton);
                    break;

                case 5:
                    JButton Fbutton = new JButton("FAN MODULE");
                    Fbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    //Fbutton.setSize(100,100);
                    sidebar.add(Fbutton);
                    break;

                case 6:
                    JButton Nbutton = new JButton("NETWORK MODULE");
                    Nbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    //Nbutton.setSize(30,75);
                    sidebar.add(Nbutton);

                    break;

                case 7:
                    JButton GEbutton = new JButton("GENERAL MODULE");
                    GEbutton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    //GEbutton.setSize(30,75);
                    sidebar.add(GEbutton);
                    break;
            }
            sidebar.add(Box.createVerticalStrut(5)); // add vertical spacing between buttons
        }

        // Add panels to the main frame
        add(mainPanel, BorderLayout.CENTER);
        add(sidebar, BorderLayout.EAST);

        // Finalize the frame
        pack();
        setLocationRelativeTo(null); // center on screen
        setVisible(true);

        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cpuModule != null) cpuModule.updateMetrics();
                if (gpuModule != null) gpuModule.updateMetrics();
                if (ramModule != null) ramModule.updateMetrics();
                if (diskModule != null) diskModule.updateMetrics();
                if (fanModule != null) fanModule.updateMetrics();
                if (networkModule != null) networkModule.updateMetrics();
            }
        });
        timer.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timer.stop();
            }
        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(APP::new);


    }
}
