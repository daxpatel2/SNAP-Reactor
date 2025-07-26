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
import java.util.HashMap;
import java.util.Map;

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
    private JSlider temperatureSlider;
    private JSlider pressureSlider;
    
    // Control rod sliders and labels
    private Map<String, JSlider> controlRodSliders =  new HashMap<>();
    private Map<String, JLabel> controlRodLabels =  new HashMap<>();
    private JPanel controlRodsPanel;
    
    private JTable controlRodsTable;
    private DefaultTableModel controlRodsTableModel;
    
    private JTextArea warningsArea;
    private JTextArea performanceArea;
    private JTextArea controlRodMovementArea;
    
    // Safety thresholds
    private static final double CRITICAL_TEMPERATURE = 500.0; // °C
    private static final double WARNING_TEMPERATURE = 400.0; // °C
    private static final double CRITICAL_PRESSURE = 20.0; // MPa
    private static final double WARNING_PRESSURE = 15.0; // MPa
    
    /**
     * Constructor - sets up the main application window.
     */
    public ReactorSimulatorApp(Reactor reactor, ReactorMonitorService monitorService) {
        this.reactor = reactor;
        this.monitorService = monitorService;

        initUI();
    }

    private void initUI() {
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
        setTitle("Nuclear Reactor Simulator - Interactive Controls");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setMinimumSize(new Dimension(1200, 700));
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
        
        // Main control sliders
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
        
        // Temperature and pressure control sliders
        temperatureSlider = new JSlider(25, 600, 25);
        temperatureSlider.setMajorTickSpacing(100);
        temperatureSlider.setMinorTickSpacing(25);
        temperatureSlider.setPaintTicks(true);
        temperatureSlider.setPaintLabels(true);
        temperatureSlider.setEnabled(false);
        
        pressureSlider = new JSlider(1, 25, 1);
        pressureSlider.setMajorTickSpacing(5);
        pressureSlider.setMinorTickSpacing(1);
        pressureSlider.setPaintTicks(true);
        pressureSlider.setPaintLabels(true);
        pressureSlider.setEnabled(false);
        
        // Control rods table
        String[] columnNames = {"Rod ID", "Insertion Level", "Operational", "Effectiveness", "Movement"};
        controlRodsTableModel = new DefaultTableModel(columnNames, 0);
        controlRodsTable = new JTable(controlRodsTableModel);
        controlRodsTable.setFillsViewportHeight(true);
        
        // Text areas
        warningsArea = new JTextArea(8, 30);
        warningsArea.setEditable(false);
        warningsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        performanceArea = new JTextArea(8, 30);
        performanceArea.setEditable(false);
        performanceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        controlRodMovementArea = new JTextArea(8, 30);
        controlRodMovementArea.setEditable(false);
        controlRodMovementArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }
    
    /**
     * Set up the layout of the GUI components.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Nuclear Reactor Simulator - Interactive Controls");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Left panel - Reactor Status and Controls
        JPanel leftPanel = createReactorStatusPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Center panel - Control Rods
        JPanel centerPanel = createControlRodsPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Right panel - Monitoring
        JPanel rightPanel = createMonitoringPanel();
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
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
        panel.setBorder(BorderFactory.createTitledBorder("Reactor Status & Controls"));
        panel.setPreferredSize(new Dimension(350, 0));
        
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
        panel.add(Box.createVerticalStrut(10));
        
        // Temperature and Pressure Controls
        JPanel environmentPanel = new JPanel();
        environmentPanel.setLayout(new BoxLayout(environmentPanel, BoxLayout.Y_AXIS));
        environmentPanel.setBorder(BorderFactory.createTitledBorder("Environment Controls"));
        
        environmentPanel.add(new JLabel("Manual Temperature (°C):"));
        environmentPanel.add(temperatureSlider);
        environmentPanel.add(Box.createVerticalStrut(10));
        environmentPanel.add(new JLabel("Manual Pressure (MPa):"));
        environmentPanel.add(pressureSlider);
        
        panel.add(environmentPanel);
        
        return panel;
    }
    
    /**
     * Create the control rods panel (center).
     */
    private JPanel createControlRodsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Control Rods - Interactive Controls"));
        
        // Control rods sliders panel
        controlRodsPanel = new JPanel();
        controlRodsPanel.setLayout(new BoxLayout(controlRodsPanel, BoxLayout.Y_AXIS));
        
        // Create sliders for each control rod
        List<ControlRod> controlRods = reactor.getControlRods();
        for (ControlRod rod : controlRods) {
            JPanel rodPanel = createControlRodSliderPanel(rod);
            controlRodsPanel.add(rodPanel);
            controlRodsPanel.add(Box.createVerticalStrut(5));
        }
        
        JScrollPane scrollPane = new JScrollPane(controlRodsPanel);
        scrollPane.setPreferredSize(new Dimension(400, 0));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a slider panel for a single control rod.
     */
    private JPanel createControlRodSliderPanel(ControlRod rod) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Control Rod " + rod.getId()));
        
        // Create slider
        JSlider slider = new JSlider(0, 100, (int) rod.getInsertionLevel());
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setEnabled(false);
        
        // Create label
        JLabel label = new JLabel(String.format("Insertion: " + rod.getInsertionLevel()));
        
        // Store references
        controlRodSliders.put(rod.getId(), slider);
        controlRodLabels.put(rod.getId(), label);
        
        // Add components
        panel.add(label, BorderLayout.NORTH);
        panel.add(slider, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the monitoring panel (right side).
     */
    private JPanel createMonitoringPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(350, 0));
        
        // Control rods table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Control Rods Status"));
        tablePanel.add(new JScrollPane(controlRodsTable), BorderLayout.CENTER);
        tablePanel.setPreferredSize(new Dimension(0, 150));
        
        panel.add(tablePanel, BorderLayout.NORTH);
        
        // Text areas panel
        JPanel textAreasPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        
        // Warnings area
        JPanel warningsPanel = new JPanel(new BorderLayout());
        warningsPanel.setBorder(BorderFactory.createTitledBorder("Health Warnings"));
        warningsPanel.add(new JScrollPane(warningsArea), BorderLayout.CENTER);
        
        // Performance area
        JPanel performancePanel = new JPanel(new BorderLayout());
        performancePanel.setBorder(BorderFactory.createTitledBorder("Performance Report"));
        performancePanel.add(new JScrollPane(performanceArea), BorderLayout.CENTER);
        
        // Control rod movement area
        JPanel movementPanel = new JPanel(new BorderLayout());
        movementPanel.setBorder(BorderFactory.createTitledBorder("Control Rod Movement Log"));
        movementPanel.add(new JScrollPane(controlRodMovementArea), BorderLayout.CENTER);
        
        textAreasPanel.add(warningsPanel);
        textAreasPanel.add(performancePanel);
        textAreasPanel.add(movementPanel);
        
        panel.add(textAreasPanel, BorderLayout.CENTER);
        
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
        
        // Main control sliders
        powerSlider.addChangeListener(e -> {
            if (reactor.isOperational()) {
                adjustPowerButton.setText("Set Power: " + powerSlider.getValue() + " MW");
            }
        });
        
        fuelConsumptionSlider.addChangeListener(e -> {
            reactor.isOperational();// Handle fuel consumption changes
        });
        
        // Temperature and pressure sliders
        temperatureSlider.addChangeListener(e -> {
            if (reactor.isOperational()) {
                double newTemp = temperatureSlider.getValue();
                reactor.setTemperature(newTemp);
                logControlRodMovement("Temperature manually adjusted to " + newTemp + "°C");
                
                // Update sliders to reflect current values
                SwingUtilities.invokeLater(() -> {
                    temperatureSlider.setValue((int) reactor.getTemperature());
                    pressureSlider.setValue((int) reactor.getPressure());
                });
            }
        });
        
        pressureSlider.addChangeListener(e -> {
            if (reactor.isOperational()) {
                double newPressure = pressureSlider.getValue();
                reactor.setPressure(newPressure);
                logControlRodMovement("Pressure manually adjusted to " + newPressure + " MPa");
                
                // Update sliders to reflect current values
                SwingUtilities.invokeLater(() -> {
                    temperatureSlider.setValue((int) reactor.getTemperature());
                    pressureSlider.setValue((int) reactor.getPressure());
                });
            }
        });
        
        // Control rod sliders
        for (Map.Entry<String, JSlider> entry : controlRodSliders.entrySet()) {
            String rodId = entry.getKey();
            JSlider slider = entry.getValue();
            
            slider.addChangeListener(e -> {
                if (reactor.isOperational()) {
                    double newLevel = slider.getValue();
                    try {
                        reactor.insertControlRod(rodId, newLevel);
                        updateControlRodLabel(rodId, newLevel);
                        logControlRodMovement("Control Rod " + rodId + " adjusted to " + newLevel + "%");
                    } catch (Exception ex) {
                        logControlRodMovement("ERROR: " + ex.getMessage());
                    }
                }
            });
        }
    }
    
    /**
     * Start the timer that updates the UI periodically.
     */
    private void startUpdateTimer() {
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    // Simulate reactor behavior
                    if (reactor.isOperational()) {
                        reactor.simulateTimeStep(1.0); // 1 second simulation
                    }
                    updateUI();
                });
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
        
        // Update progress bars with color coding
        updateProgressBars();
        
        // Update health score
        ReactorHealthReport healthReport = monitorService.analyzeHealth(reactor);
        healthScoreLabel.setText(String.format("Health Score: " + healthReport.getHealthScore()));
        
        // Update last update time
        lastUpdateLabel.setText("Last Update: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        // Update warnings area
        updateWarningsArea(healthReport);
        
        // Update performance area
        updatePerformanceArea();
        
        // Update control rods table
        updateControlRodsTable();
        
        // Update control rod sliders and labels
        updateControlRodControls();
        
        // Update button states
        updateButtonStates();
        
        // Update slider states
        updateSliderStates();
    }
    
    /**
     * Update progress bars with color coding based on safety thresholds.
     */
    private void updateProgressBars() {
        double temp = reactor.getTemperature();
        double pressure = reactor.getPressure();
        double power = reactor.getPowerOutput();
        double fuel = reactor.getFuelLevel();
        
        // Temperature bar
        int tempValue = (int) Math.min(temp / 600.0 * 100, 100);
        temperatureBar.setValue(tempValue);
        if (temp >= CRITICAL_TEMPERATURE) {
            temperatureBar.setForeground(Color.RED);
        } else if (temp >= WARNING_TEMPERATURE) {
            temperatureBar.setForeground(Color.ORANGE);
        } else {
            temperatureBar.setForeground(new Color(0, 150, 0));
        }
        
        // Pressure bar
        int pressureValue = (int) Math.min(pressure / 25.0 * 100, 100);
        pressureBar.setValue(pressureValue);
        if (pressure >= CRITICAL_PRESSURE) {
            pressureBar.setForeground(Color.RED);
        } else if (pressure >= WARNING_PRESSURE) {
            pressureBar.setForeground(Color.ORANGE);
        } else {
            pressureBar.setForeground(new Color(0, 150, 0));
        }
        
        // Power bar
        int powerValue = (int) (power / 1200.0 * 100);
        powerBar.setValue(powerValue);
        powerBar.setForeground(new Color(0, 100, 200));
        
        // Fuel bar
        int fuelValue = (int) fuel;
        fuelBar.setValue(fuelValue);
        if (fuel <= 20) {
            fuelBar.setForeground(Color.RED);
        } else if (fuel <= 50) {
            fuelBar.setForeground(Color.ORANGE);
        } else {
            fuelBar.setForeground(new Color(0, 150, 0));
        }
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
        
        // Add temperature and pressure warnings
        double temp = reactor.getTemperature();
        double pressure = reactor.getPressure();
        
        warnings.append("\n=== ENVIRONMENTAL STATUS ===\n");
        if (temp >= CRITICAL_TEMPERATURE) {
            warnings.append("🚨 CRITICAL TEMPERATURE: ").append(String.format("%.1f°C", temp)).append("\n");
        } else if (temp >= WARNING_TEMPERATURE) {
            warnings.append("⚠ HIGH TEMPERATURE: ").append(String.format("%.1f°C", temp)).append("\n");
        } else {
            warnings.append("✅ Temperature Normal: ").append(String.format("%.1f°C", temp)).append("\n");
        }
        
        if (pressure >= CRITICAL_PRESSURE) {
            warnings.append("🚨 CRITICAL PRESSURE: ").append(String.format("%.1f MPa", pressure)).append("\n");
        } else if (pressure >= WARNING_PRESSURE) {
            warnings.append("⚠ HIGH PRESSURE: ").append(String.format("%.1f MPa", pressure)).append("\n");
        } else {
            warnings.append("✅ Pressure Normal: ").append(String.format("%.1f MPa", pressure)).append("\n");
        }
        
        warningsArea.setText(warnings.toString());
    }
    
    /**
     * Update the performance area with current performance metrics.
     */
    private void updatePerformanceArea() {
        PerformanceReport report = monitorService.generatePerformanceReport(reactor);

        String performance = "=== PERFORMANCE REPORT ===\n\n" +
                "Power Output: " + String.format("%.1f MW", report.getCurrentPower()) + "\n" +
                "Efficiency: " + String.format("%.1f%%", report.getCurrentEfficiency()) + "\n" +
                "Fuel Level: " + String.format("%.1f%%", report.getFuelLevel()) + "\n" +
                "Operational Hours: " + report.getOperationalHours() + "\n" +
                "Performance Grade: " + report.getPerformanceGrade() + "\n" +
                "Power Utilization: " + String.format("%.1f%%", report.getPowerUtilization()) + "\n" +
                "\n=== OPERATIONAL STATUS ===\n" +
                "Optimal Power: " + (report.isOperatingAtOptimalPower() ? "✅" : "❌") + "\n" +
                "Optimal Efficiency: " + (report.isOperatingAtOptimalEfficiency() ? "✅" : "❌") + "\n" +
                "Fuel Refill Needed: " + (report.needsFuelRefill() ? "⚠" : "✅") + "\n";
        
        performanceArea.setText(performance);
    }
    
    /**
     * Update the control rods table with current data.
     */
    private void updateControlRodsTable() {
        controlRodsTableModel.setRowCount(0);
        List<ControlRod> controlRods = reactor.getControlRods();
        
        for (ControlRod rod : controlRods) {
            String movementStatus = rod.getCurrentInsertionSpeed() > 0 ? "Moving" : "Static";
            controlRodsTableModel.addRow(new Object[]{
                rod.getId(),
                String.format("%.1f%%", rod.getInsertionLevel()),
                rod.isOperational() ? "✅" : "❌",
                String.format("%.1f%%", rod.getEffectiveness() * 100),
                movementStatus
            });
        }
    }
    
    /**
     * Update control rod sliders and labels.
     */
    private void updateControlRodControls() {
        List<ControlRod> controlRods = reactor.getControlRods();
        
        for (ControlRod rod : controlRods) {
            String rodId = rod.getId();
            JSlider slider = controlRodSliders.get(rodId);
            JLabel label = controlRodLabels.get(rodId);
            
            if (slider != null && label != null) {
                // Update slider value (without triggering change listener)
                slider.setValue((int) rod.getInsertionLevel());
                
                // Update label
                updateControlRodLabel(rodId, rod.getInsertionLevel());
            }
        }
    }
    
    /**
     * Update a specific control rod label.
     */
    private void updateControlRodLabel(String rodId, double insertionLevel) {
        JLabel label = controlRodLabels.get(rodId);
        if (label != null) {
            label.setText(String.format("Insertion: %.1f%%", insertionLevel));
        }
    }
    
    /**
     * Log control rod movement to the movement area.
     */
    private void logControlRodMovement(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logEntry = "[" + timestamp + "] " + message + "\n";
        
        SwingUtilities.invokeLater(() -> {
            controlRodMovementArea.append(logEntry);
            // Auto-scroll to bottom
            controlRodMovementArea.setCaretPosition(controlRodMovementArea.getDocument().getLength());
        });
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
        boolean isOperational = reactor.isOperational();
        
        powerSlider.setEnabled(isOperational);
        fuelConsumptionSlider.setEnabled(isOperational);
        temperatureSlider.setEnabled(isOperational);
        pressureSlider.setEnabled(isOperational);
        
        // Update control rod sliders
        for (JSlider slider : controlRodSliders.values()) {
            slider.setEnabled(isOperational);
        }
        
        // Update slider values to reflect current reactor state
        if (isOperational) {
            temperatureSlider.setValue((int) reactor.getTemperature());
            pressureSlider.setValue((int) reactor.getPressure());
        }
    }
    
    // Event handlers
    protected void handleStartUp() {
        try {
            reactor.startUp();
            showMessage("Reactor Startup", "Reactor is starting up...", JOptionPane.INFORMATION_MESSAGE);
            logControlRodMovement("Reactor startup initiated");
        } catch (IllegalStateException e) {
            showMessage("Startup Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void handleShutdown() {
        try {
            reactor.shutdown();
            showMessage("Reactor Shutdown", "Reactor has been shut down.", JOptionPane.INFORMATION_MESSAGE);
            logControlRodMovement("Reactor shutdown initiated");
        } catch (IllegalStateException e) {
            showMessage("Shutdown Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void handleEmergencyShutdown() {
        reactor.emergencyShutdown();
        showMessage("EMERGENCY SHUTDOWN", "Emergency shutdown initiated!", JOptionPane.WARNING_MESSAGE);
        logControlRodMovement("EMERGENCY SHUTDOWN ACTIVATED");
    }
    
    protected void handleAdjustPower() {
        double targetPower = powerSlider.getValue();
        try {
            reactor.adjustPower(targetPower);
            showMessage("Power Adjustment", 
                String.format("Power adjusted to %.1f MW", targetPower),
                JOptionPane.INFORMATION_MESSAGE);
            logControlRodMovement("Power adjusted to " + targetPower + " MW");
        } catch (IllegalArgumentException | IllegalStateException e) {
            showMessage("Power Adjustment Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void handleMaintenance() {
        try {
            reactor.performMaintenance();
            showMessage("Maintenance Complete", "Maintenance has been completed.", JOptionPane.INFORMATION_MESSAGE);
            logControlRodMovement("Maintenance completed");
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
            ReactorSimulatorApp app = new ReactorSimulatorApp(new Reactor("1","3 Mile Island"),new ReactorMonitorService());
            app.setVisible(true);
        });
    }
} 