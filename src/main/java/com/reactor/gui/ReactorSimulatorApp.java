package com.reactor.gui;

import com.reactor.model.Reactor;
import com.reactor.model.ControlRod;
import com.reactor.service.ReactorMonitorService;
import com.reactor.service.ReactorHealthReport;
import com.reactor.service.PerformanceReport;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main Java Swing application for the Reactor Simulator GUI.
 * Provides a comprehensive interface for monitoring and controlling the nuclear reactor.
 */
public class ReactorSimulatorApp extends JFrame {
    
    // Reactor and services
    protected Reactor reactor;
    protected ReactorMonitorService monitorService;
    protected Timer updateTimer;
    
    // UI Components
    private JLabel statusLabel;
    private JLabel temperatureLabel;
    private JLabel pressureLabel;
    private JLabel powerLabel;
    private JLabel fuelLabel;
    private JLabel efficiencyLabel;
    private JLabel healthScoreLabel;
    private JLabel lastUpdateLabel;
    
    private JProgressBar temperatureBar;
    private JProgressBar pressureBar;
    private JProgressBar powerBar;
    private JProgressBar fuelBar;
    
    private JButton startUpButton;
    private JButton shutdownButton;
    private JButton emergencyShutdownButton;
    private JButton adjustPowerButton;
    private JButton maintenanceButton;
    
    private JSlider powerSlider;
    private JSlider fuelConsumptionSlider;
    
    private JTable controlRodsTable;
    private DefaultTableModel controlRodsTableModel;
    
    private JTextArea warningsArea;
    private JTextArea performanceArea;
    
    /**
     * Constructor - sets up the main application window.
     */
    public ReactorSimulatorApp() {
        // Initialize reactor and services
        reactor = new Reactor("R-001", "Main Reactor");
        monitorService = new ReactorMonitorService();
        
        // Set up the main window
        setupMainWindow();
        setupUIComponents();
        setupLayout();
        setupEventHandlers();
        
        // Start the update timer
        startUpdateTimer();
        
        // Initial update
        updateUI();
    }
    
    /**
     * Set up the main window properties.
     */
    private void setupMainWindow() {
        setTitle("Nuclear Reactor Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
    }
    
    /**
     * Create and configure all UI components.
     */
    private void setupUIComponents() {
        // Labels
        statusLabel = new JLabel("Status: SHUTDOWN");
        temperatureLabel = new JLabel("Temperature: 25.0°C");
        pressureLabel = new JLabel("Pressure: 0.1 MPa");
        powerLabel = new JLabel("Power: 0.0 MW");
        fuelLabel = new JLabel("Fuel: 100.0%");
        efficiencyLabel = new JLabel("Efficiency: 0.0%");
        healthScoreLabel = new JLabel("Health Score: 100.0");
        lastUpdateLabel = new JLabel("Last Update: --:--:--");
        
        // Progress bars
        temperatureBar = new JProgressBar(0, 100);
        pressureBar = new JProgressBar(0, 100);
        powerBar = new JProgressBar(0, 100);
        fuelBar = new JProgressBar(0, 100);
        
        // Configure progress bars
        temperatureBar.setStringPainted(true);
        pressureBar.setStringPainted(true);
        powerBar.setStringPainted(true);
        fuelBar.setStringPainted(true);
        
        // Buttons
        startUpButton = new JButton("Start Up Reactor");
        shutdownButton = new JButton("Shutdown Reactor");
        emergencyShutdownButton = new JButton("EMERGENCY SHUTDOWN");
        adjustPowerButton = new JButton("Set Power: 0 MW");
        maintenanceButton = new JButton("Perform Maintenance");
        
        // Style emergency button
        emergencyShutdownButton.setBackground(Color.RED);
        emergencyShutdownButton.setForeground(Color.WHITE);
        emergencyShutdownButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Sliders
        powerSlider = new JSlider(0, 1200, 0);
        powerSlider.setMajorTickSpacing(200);
        powerSlider.setMinorTickSpacing(50);
        powerSlider.setPaintTicks(true);
        powerSlider.setPaintLabels(true);
        powerSlider.setEnabled(false);
        
        fuelConsumptionSlider = new JSlider(0, 24, 1);
        fuelConsumptionSlider.setMajorTickSpacing(6);
        fuelConsumptionSlider.setMinorTickSpacing(1);
        fuelConsumptionSlider.setPaintTicks(true);
        fuelConsumptionSlider.setPaintLabels(true);
        
        // Control rods table
        String[] columnNames = {"Rod ID", "Insertion Level", "Operational", "Effectiveness"};
        controlRodsTableModel = new DefaultTableModel(columnNames, 0);
        controlRodsTable = new JTable(controlRodsTableModel);
        controlRodsTable.setFillsViewportHeight(true);
        
        // Text areas
        warningsArea = new JTextArea(10, 30);
        warningsArea.setEditable(false);
        warningsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        performanceArea = new JTextArea(10, 30);
        performanceArea.setEditable(false);
        performanceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }
    
    /**
     * Set up the layout of the GUI components.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Nuclear Reactor Simulator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Left panel - Reactor Status
        JPanel leftPanel = createReactorStatusPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Right panel - Monitoring
        JPanel rightPanel = createMonitoringPanel();
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusBar.add(new JLabel("Ready"));
        add(statusBar, BorderLayout.SOUTH);
    }
    
    /**
     * Create the reactor status panel (left side).
     */
    private JPanel createReactorStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Reactor Status"));
        panel.setPreferredSize(new Dimension(400, 0));
        
        // Status grid
        JPanel statusGrid = new JPanel(new GridLayout(8, 2, 10, 5));
        statusGrid.add(new JLabel("Status:"));
        statusGrid.add(statusLabel);
        statusGrid.add(new JLabel("Temperature:"));
        statusGrid.add(temperatureLabel);
        statusGrid.add(new JLabel("Pressure:"));
        statusGrid.add(pressureLabel);
        statusGrid.add(new JLabel("Power Output:"));
        statusGrid.add(powerLabel);
        statusGrid.add(new JLabel("Fuel Level:"));
        statusGrid.add(fuelLabel);
        statusGrid.add(new JLabel("Efficiency:"));
        statusGrid.add(efficiencyLabel);
        statusGrid.add(new JLabel("Health Score:"));
        statusGrid.add(healthScoreLabel);
        statusGrid.add(new JLabel("Last Update:"));
        statusGrid.add(lastUpdateLabel);
        
        panel.add(statusGrid);
        panel.add(Box.createVerticalStrut(10));
        
        // Progress bars
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBorder(BorderFactory.createTitledBorder("System Status"));
        
        progressPanel.add(new JLabel("Temperature:"));
        progressPanel.add(temperatureBar);
        progressPanel.add(Box.createVerticalStrut(5));
        progressPanel.add(new JLabel("Pressure:"));
        progressPanel.add(pressureBar);
        progressPanel.add(Box.createVerticalStrut(5));
        progressPanel.add(new JLabel("Power Output:"));
        progressPanel.add(powerBar);
        progressPanel.add(Box.createVerticalStrut(5));
        progressPanel.add(new JLabel("Fuel Level:"));
        progressPanel.add(fuelBar);
        
        panel.add(progressPanel);
        panel.add(Box.createVerticalStrut(10));
        
        // Control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Reactor Controls"));
        
        controlPanel.add(startUpButton);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(shutdownButton);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(emergencyShutdownButton);
        controlPanel.add(Box.createVerticalStrut(5));
        controlPanel.add(maintenanceButton);
        
        panel.add(controlPanel);
        panel.add(Box.createVerticalStrut(10));
        
        // Power control
        JPanel powerPanel = new JPanel();
        powerPanel.setLayout(new BoxLayout(powerPanel, BoxLayout.Y_AXIS));
        powerPanel.setBorder(BorderFactory.createTitledBorder("Power Control"));
        
        powerPanel.add(new JLabel("Target Power (MW):"));
        powerPanel.add(powerSlider);
        powerPanel.add(Box.createVerticalStrut(5));
        powerPanel.add(adjustPowerButton);
        powerPanel.add(Box.createVerticalStrut(10));
        powerPanel.add(new JLabel("Fuel Consumption Rate (hours):"));
        powerPanel.add(fuelConsumptionSlider);
        
        panel.add(powerPanel);
        
        return panel;
    }
    
    /**
     * Create the monitoring panel (right side).
     */
    private JPanel createMonitoringPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Control rods table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Control Rods Status"));
        tablePanel.add(new JScrollPane(controlRodsTable), BorderLayout.CENTER);
        tablePanel.setPreferredSize(new Dimension(0, 200));
        
        panel.add(tablePanel, BorderLayout.NORTH);
        
        // Warnings and performance panels
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Warnings area
        JPanel warningsPanel = new JPanel(new BorderLayout());
        warningsPanel.setBorder(BorderFactory.createTitledBorder("Health Warnings"));
        warningsPanel.add(new JScrollPane(warningsArea), BorderLayout.CENTER);
        
        // Performance area
        JPanel performancePanel = new JPanel(new BorderLayout());
        performancePanel.setBorder(BorderFactory.createTitledBorder("Performance Report"));
        performancePanel.add(new JScrollPane(performanceArea), BorderLayout.CENTER);
        
        bottomPanel.add(warningsPanel);
        bottomPanel.add(performancePanel);
        
        panel.add(bottomPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Set up event handlers for buttons and controls.
     */
    private void setupEventHandlers() {
        // Reactor control buttons
        startUpButton.addActionListener(e -> handleStartUp());
        shutdownButton.addActionListener(e -> handleShutdown());
        emergencyShutdownButton.addActionListener(e -> handleEmergencyShutdown());
        adjustPowerButton.addActionListener(e -> handleAdjustPower());
        maintenanceButton.addActionListener(e -> handleMaintenance());
        
        // Slider change handlers
        powerSlider.addChangeListener(e -> {
            if (reactor.isOperational()) {
                adjustPowerButton.setText("Set Power: " + powerSlider.getValue() + " MW");
            }
        });
        
        fuelConsumptionSlider.addChangeListener(e -> {
            if (reactor.isOperational()) {
                // Handle fuel consumption changes
            }
        });
    }
    
    /**
     * Start the timer that updates the UI periodically.
     */
    private void startUpdateTimer() {
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateUI());
            }
        }, 0, 1000); // Update every second
    }
    
    /**
     * Update all UI components with current reactor state.
     */
    private void updateUI() {
        // Update status labels
        statusLabel.setText("Status: " + reactor.getStatus());
        temperatureLabel.setText(String.format("Temperature: %.1f°C", reactor.getTemperature()));
        pressureLabel.setText(String.format("Pressure: %.1f MPa", reactor.getPressure()));
        powerLabel.setText(String.format("Power: %.1f MW", reactor.getPowerOutput()));
        fuelLabel.setText(String.format("Fuel: %.1f%%", reactor.getFuelLevel()));
        efficiencyLabel.setText(String.format("Efficiency: %.1f%%", reactor.getEfficiency()));
        
        // Update progress bars
        temperatureBar.setValue((int) Math.min(reactor.getTemperature() / 600.0 * 100, 100));
        pressureBar.setValue((int) Math.min(reactor.getPressure() / 25.0 * 100, 100));
        powerBar.setValue((int) (reactor.getPowerOutput() / 1200.0 * 100));
        fuelBar.setValue((int) reactor.getFuelLevel());
        
        // Update health score
        ReactorHealthReport healthReport = monitorService.analyzeHealth(reactor);
        healthScoreLabel.setText(String.format("Health Score: %.1f", healthReport.getHealthScore()));
        
        // Update last update time
        lastUpdateLabel.setText("Last Update: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        // Update warnings area
        updateWarningsArea(healthReport);
        
        // Update performance area
        updatePerformanceArea();
        
        // Update control rods table
        updateControlRodsTable();
        
        // Update button states
        updateButtonStates();
        
        // Update slider states
        updateSliderStates();
    }
    
    /**
     * Update the warnings area with current health warnings.
     */
    private void updateWarningsArea(ReactorHealthReport healthReport) {
        StringBuilder warnings = new StringBuilder();
        warnings.append("=== REACTOR HEALTH WARNINGS ===\n\n");
        
        if (healthReport.hasWarnings()) {
            for (String warning : healthReport.getWarnings()) {
                warnings.append("⚠ ").append(warning).append("\n");
            }
        } else {
            warnings.append("✅ All systems operating normally\n");
        }
        
        warnings.append("\n=== SAFETY STATUS ===\n");
        if (monitorService.isOperatingSafely(reactor)) {
            warnings.append("✅ Reactor operating safely\n");
        } else {
            warnings.append("🚨 SAFETY VIOLATION DETECTED\n");
        }
        
        warningsArea.setText(warnings.toString());
    }
    
    /**
     * Update the performance area with current performance metrics.
     */
    private void updatePerformanceArea() {
        PerformanceReport report = monitorService.generatePerformanceReport(reactor);
        StringBuilder performance = new StringBuilder();
        
        performance.append("=== PERFORMANCE REPORT ===\n\n");
        performance.append("Power Output: ").append(String.format("%.1f MW", report.getCurrentPower())).append("\n");
        performance.append("Efficiency: ").append(String.format("%.1f%%", report.getCurrentEfficiency())).append("\n");
        performance.append("Fuel Level: ").append(String.format("%.1f%%", report.getFuelLevel())).append("\n");
        performance.append("Operational Hours: ").append(report.getOperationalHours()).append("\n");
        performance.append("Performance Grade: ").append(report.getPerformanceGrade()).append("\n");
        performance.append("Power Utilization: ").append(String.format("%.1f%%", report.getPowerUtilization())).append("\n");
        
        performance.append("\n=== OPERATIONAL STATUS ===\n");
        performance.append("Optimal Power: ").append(report.isOperatingAtOptimalPower() ? "✅" : "❌").append("\n");
        performance.append("Optimal Efficiency: ").append(report.isOperatingAtOptimalEfficiency() ? "✅" : "❌").append("\n");
        performance.append("Fuel Refill Needed: ").append(report.needsFuelRefill() ? "⚠" : "✅").append("\n");
        
        performanceArea.setText(performance.toString());
    }
    
    /**
     * Update the control rods table with current data.
     */
    private void updateControlRodsTable() {
        controlRodsTableModel.setRowCount(0);
        List<ControlRod> controlRods = reactor.getControlRods();
        
        for (ControlRod rod : controlRods) {
            controlRodsTableModel.addRow(new Object[]{
                rod.getId(),
                String.format("%.1f%%", rod.getInsertionLevel()),
                rod.isOperational() ? "✅" : "❌",
                String.format("%.1f%%", rod.getEffectiveness() * 100)
            });
        }
    }
    
    /**
     * Update button states based on reactor status.
     */
    private void updateButtonStates() {
        boolean isShutdown = reactor.getStatus() == Reactor.ReactorStatus.SHUTDOWN;
        boolean isMaintenance = reactor.getStatus() == Reactor.ReactorStatus.MAINTENANCE;
        boolean isOperational = reactor.isOperational();
        boolean isEmergencyShutdown = reactor.getStatus() == Reactor.ReactorStatus.EMERGENCY_SHUTDOWN;
        
        startUpButton.setEnabled(isShutdown || isMaintenance);
        shutdownButton.setEnabled(isOperational && !isEmergencyShutdown);
        emergencyShutdownButton.setEnabled(!isEmergencyShutdown);
        adjustPowerButton.setEnabled(isOperational);
        maintenanceButton.setEnabled(isMaintenance);
    }
    
    /**
     * Update slider states based on reactor status.
     */
    private void updateSliderStates() {
        powerSlider.setEnabled(reactor.isOperational());
        fuelConsumptionSlider.setEnabled(reactor.isOperational());
    }
    
    // Event handlers
    protected void handleStartUp() {
        try {
            reactor.startUp();
            showMessage("Reactor Startup", "Reactor is starting up...", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException e) {
            showMessage("Startup Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void handleShutdown() {
        try {
            reactor.shutdown();
            showMessage("Reactor Shutdown", "Reactor has been shut down.", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException e) {
            showMessage("Shutdown Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void handleEmergencyShutdown() {
        reactor.emergencyShutdown();
        showMessage("EMERGENCY SHUTDOWN", "Emergency shutdown initiated!", JOptionPane.WARNING_MESSAGE);
    }
    
    protected void handleAdjustPower() {
        double targetPower = powerSlider.getValue();
        try {
            reactor.adjustPower(targetPower);
            showMessage("Power Adjustment", 
                String.format("Power adjusted to %.1f MW", targetPower), 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException e) {
            showMessage("Power Adjustment Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void handleMaintenance() {
        try {
            reactor.performMaintenance();
            showMessage("Maintenance Complete", "Maintenance has been completed.", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException e) {
            showMessage("Maintenance Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Show a message dialog with the given information.
     */
    private void showMessage(String title, String content, int messageType) {
        JOptionPane.showMessageDialog(this, content, title, messageType);
    }
    
    /**
     * Stop the update timer when the application is closing.
     */
    @Override
    public void dispose() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        super.dispose();
    }
    
    /**
     * Get the reactor instance (for testing purposes).
     */
    public Reactor getReactor() {
        return reactor;
    }
    
    /**
     * Get the monitor service instance (for testing purposes).
     */
    public ReactorMonitorService getMonitorService() {
        return monitorService;
    }
    
    /**
     * Main method to launch the application.
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Launch the application
        SwingUtilities.invokeLater(() -> {
            ReactorSimulatorApp app = new ReactorSimulatorApp();
            app.setVisible(true);
        });
    }
} 