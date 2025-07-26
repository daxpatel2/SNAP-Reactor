package com.reactor.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a nuclear reactor with various operational parameters.
 * This class provides a rich set of methods and properties for testing practice.
 */
public class Reactor {
    private String id;
    private String name;
    private double temperature; // in Celsius
    private double pressure; // in MPa
    private double powerOutput; // in MW
    private double fuelLevel; // percentage
    private ReactorStatus status;
    private final List<ControlRod> controlRods;
    private LocalDateTime lastMaintenance;
    private int operationalHours;
    
    public enum ReactorStatus {
        SHUTDOWN, STARTING_UP, OPERATIONAL, EMERGENCY_SHUTDOWN, MAINTENANCE
    }
    
    public Reactor(String id, String name) {
        this.id = id;
        this.name = name;
        this.temperature = 25.0; // Room temperature
        this.pressure = 0.1; // Atmospheric pressure
        this.powerOutput = 0.0;
        this.fuelLevel = 100.0;
        this.status = ReactorStatus.SHUTDOWN;
        this.controlRods = new ArrayList<>();
        this.lastMaintenance = LocalDateTime.now();
        this.operationalHours = 0;
        
        // Initialize control rods
        for (int i = 0; i < 10; i++) {
            controlRods.add(new ControlRod("CR-" + (i + 1), 100.0));
        }
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { 
        if (temperature < 0) {
            throw new IllegalArgumentException("Temperature cannot be negative");
        }
        this.temperature = temperature; 
    }
    
    public double getPressure() { return pressure; }
    public void setPressure(double pressure) { 
        if (pressure < 0) {
            throw new IllegalArgumentException("Pressure cannot be negative");
        }
        this.pressure = pressure; 
    }
    
    public double getPowerOutput() { return powerOutput; }
    public void setPowerOutput(double powerOutput) { 
        if (powerOutput < 0) {
            throw new IllegalArgumentException("Power output cannot be negative");
        }
        this.powerOutput = powerOutput; 
    }
    
    public double getFuelLevel() { return fuelLevel; }
    public void setFuelLevel(double fuelLevel) { 
        if (fuelLevel < 0 || fuelLevel > 100) {
            throw new IllegalArgumentException("Fuel level must be between 0 and 100");
        }
        this.fuelLevel = fuelLevel; 
    }
    
    public ReactorStatus getStatus() { return status; }
    public void setStatus(ReactorStatus status) { this.status = status; }
    
    public List<ControlRod> getControlRods() { return new ArrayList<>(controlRods); }
    
    public LocalDateTime getLastMaintenance() { return lastMaintenance; }
    public void setLastMaintenance(LocalDateTime lastMaintenance) { this.lastMaintenance = lastMaintenance; }
    
    public int getOperationalHours() { return operationalHours; }
    public void setOperationalHours(int operationalHours) { 
        if (operationalHours < 0) {
            throw new IllegalArgumentException("Operational hours cannot be negative");
        }
        this.operationalHours = operationalHours; 
    }
    
    // Business Logic Methods
    public void startUp() {
        if (status != ReactorStatus.SHUTDOWN && status != ReactorStatus.MAINTENANCE) {
            throw new IllegalStateException("Reactor can only be started from SHUTDOWN or MAINTENANCE status");
        }
        if (fuelLevel < 10.0) {
            throw new IllegalStateException("Insufficient fuel level for startup");
        }
        status = ReactorStatus.STARTING_UP;
        temperature = 100.0;
        pressure = 1.0;
    }
    
    public void reachOperational() {
        if (status != ReactorStatus.STARTING_UP) {
            throw new IllegalStateException("Reactor must be in STARTING_UP status to reach operational");
        }
        status = ReactorStatus.OPERATIONAL;
        temperature = 300.0;
        pressure = 15.0;
        powerOutput = 1000.0; // 1000 MW
    }
    
    public void shutdown() {
        if (status == ReactorStatus.EMERGENCY_SHUTDOWN) {
            throw new IllegalStateException("Reactor is already in emergency shutdown");
        }
        status = ReactorStatus.SHUTDOWN;
        temperature = 50.0;
        pressure = 0.5;
        powerOutput = 0.0;
    }
    
    public void emergencyShutdown() {
        status = ReactorStatus.EMERGENCY_SHUTDOWN;
        temperature = 25.0;
        pressure = 0.1;
        powerOutput = 0.0;
        // Insert all control rods
        controlRods.forEach(rod -> rod.setInsertionLevel(100.0));
    }
    
    public void adjustPower(double targetPower) {
        if (status != ReactorStatus.OPERATIONAL) {
            throw new IllegalStateException("Power can only be adjusted when reactor is operational");
        }
        if (targetPower < 0 || targetPower > 1200) {
            throw new IllegalArgumentException("Power must be between 0 and 1200 MW");
        }
        
        powerOutput = targetPower;
        
        // Calculate temperature and pressure directly instead of using simulatePowerEffects
        // to avoid potential circular calls
        double baseTemp = 25.0;
        double powerTempIncrease = (targetPower / 1200.0) * 400.0;
        temperature = baseTemp + powerTempIncrease;

        double basePressure = 0.1;
        double powerPressureIncrease = (targetPower / 1200.0) * 19.9;
        pressure = basePressure + powerPressureIncrease;
    }
    
    /**
     * Simulate the effects of power changes on temperature and pressure.
     */
    private void simulatePowerEffects(double power) {
        // Base temperature at 25°C, increases with power
        double baseTemp = 25.0;
        double powerTempIncrease = (power / 1200.0) * 400.0; // Max 425°C at full power
        temperature = baseTemp + powerTempIncrease;
        
        // Base pressure at 0.1 MPa, increases with power
        double basePressure = 0.1;
        double powerPressureIncrease = (power / 1200.0) * 19.9; // Max 20 MPa at full power
        pressure = basePressure + powerPressureIncrease;
        
        // We don't call simulateControlRodEffects() here to avoid infinite recursion
    }

    /**
     * Simulate the effects of control rod positions on reactor behavior.
     */
    private void simulateControlRodEffects() {
        if (status != ReactorStatus.OPERATIONAL) return;

        double averageInsertion = controlRods.stream()
                .mapToDouble(ControlRod::getInsertionLevel)
                .average()
                .orElse(0.0);

        // Control rods affect power output
        double powerReduction = (averageInsertion / 100.0) * 0.3; // Max 30% power reduction
        double adjustedPower = powerOutput * (1 - powerReduction);

        // Update power without calling simulatePowerEffects to avoid infinite recursion
        powerOutput = Math.max(0, adjustedPower);

        // Calculate temperature and pressure directly
        double baseTemp = 25.0;
        double powerTempIncrease = (powerOutput / 1200.0) * 400.0;
        temperature = baseTemp + powerTempIncrease;

        double basePressure = 0.1;
        double powerPressureIncrease = (powerOutput / 1200.0) * 19.9;
        pressure = basePressure + powerPressureIncrease;
    }
    
    public void insertControlRod(String rodId, double insertionLevel) {
        ControlRod rod = controlRods.stream()
                .filter(r -> r.getId().equals(rodId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Control rod not found: " + rodId));
        
        // Simulate gradual movement
        double currentLevel = rod.getInsertionLevel();
        double movement = insertionLevel - currentLevel;
        
        if (Math.abs(movement) > 0.1) {
            // Set movement speed based on distance
            double speed = Math.min(5.0, Math.abs(movement) / 10.0); // 5% per second max
            rod.setCurrentInsertionSpeed(movement > 0 ? speed : -speed);
        } else {
            rod.setCurrentInsertionSpeed(0.0);
        }
        
        rod.setInsertionLevel(insertionLevel);
        
        // Simulate effects of control rod movement
        if (status == ReactorStatus.OPERATIONAL) {
            simulateControlRodEffects();
        }
    }
    
    public boolean isOperational() {
        return status == ReactorStatus.OPERATIONAL;
    }
    
    public boolean isInDangerZone() {
        return temperature > 500.0 || pressure > 20.0 || fuelLevel < 5.0;
    }
    
    public double getEfficiency() {
        if (powerOutput == 0) return 0.0;
        return (powerOutput / 1000.0) * (fuelLevel / 100.0) * 100.0;
    }
    
    public void consumeFuel(double hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Hours cannot be negative");
        }
        
        double fuelConsumption = (powerOutput / 1000.0) * hours * 0.1;
        fuelLevel = Math.max(0, fuelLevel - fuelConsumption);
        operationalHours += (int) hours;
        
        if (fuelLevel < 5.0) {
            status = ReactorStatus.MAINTENANCE;
        }
    }
    
    public void performMaintenance() {
        if (status != ReactorStatus.MAINTENANCE) {
            throw new IllegalStateException("Maintenance can only be performed when reactor is in MAINTENANCE status");
        }
        
        fuelLevel = 100.0;
        lastMaintenance = LocalDateTime.now();
        status = ReactorStatus.SHUTDOWN;
    }
    
    /**
     * Simulate automatic reactor behavior over time.
     */
    public void simulateTimeStep(double seconds) {
        if (status != ReactorStatus.OPERATIONAL) return;
        
        // Simulate control rod movement
        for (ControlRod rod : controlRods) {
            if (rod.getCurrentInsertionSpeed() != 0) {
                double currentLevel = rod.getInsertionLevel();
                double newLevel = currentLevel + (rod.getCurrentInsertionSpeed() * seconds);
                rod.setInsertionLevel(Math.max(0, Math.min(100, newLevel)));
                
                // Stop movement if target reached
                if (Math.abs(newLevel - currentLevel) < 0.1) {
                    rod.setCurrentInsertionSpeed(0.0);
                }
            }
        }
        
        // Simulate fuel consumption
        double fuelConsumption = (powerOutput / 1000.0) * (seconds / 3600.0) * 0.1;
        fuelLevel = Math.max(0, fuelLevel - fuelConsumption);
        
        // Simulate temperature and pressure fluctuations
        simulateEnvironmentalFluctuations(seconds);
        
        // Check for maintenance needs
        if (fuelLevel < 5.0) {
            status = ReactorStatus.MAINTENANCE;
        }
    }
    
    /**
     * Simulate environmental fluctuations in temperature and pressure.
     */
    private void simulateEnvironmentalFluctuations(double seconds) {
        // Small random fluctuations
        double tempFluctuation = (Math.random() - 0.5) * 2.0; // ±1°C
        double pressureFluctuation = (Math.random() - 0.5) * 0.2; // ±0.1 MPa
        
        temperature += tempFluctuation * (seconds / 60.0); // Gradual change
        pressure += pressureFluctuation * (seconds / 60.0);
        
        // Ensure values stay within reasonable bounds
        temperature = Math.max(25.0, Math.min(600.0, temperature));
        pressure = Math.max(0.1, Math.min(25.0, pressure));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reactor reactor = (Reactor) o;
        return Objects.equals(id, reactor.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("Reactor{id='%s', name='%s', status=%s, power=%.1f MW, temp=%.1f°C}", (Object) id, (Object) name, (Object) status, (Object) powerOutput, (Object) temperature);
    }
} 