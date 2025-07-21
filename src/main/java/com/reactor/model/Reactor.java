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
    private List<ControlRod> controlRods;
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
        
        // Adjust temperature based on power
        temperature = 300.0 + (targetPower / 1000.0) * 200.0;
        
        // Adjust pressure based on power
        pressure = 15.0 + (targetPower / 1000.0) * 5.0;
    }
    
    public void insertControlRod(String rodId, double insertionLevel) {
        ControlRod rod = controlRods.stream()
                .filter(r -> r.getId().equals(rodId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Control rod not found: " + rodId));
        
        rod.setInsertionLevel(insertionLevel);
        
        // Adjust power based on control rod insertion
        if (status == ReactorStatus.OPERATIONAL) {
            double averageInsertion = controlRods.stream()
                    .mapToDouble(ControlRod::getInsertionLevel)
                    .average()
                    .orElse(0.0);
            
            double powerReduction = (averageInsertion / 100.0) * 0.5;
            powerOutput = Math.max(0, powerOutput * (1 - powerReduction));
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
        return String.format("Reactor{id='%s', name='%s', status=%s, power=%.1f MW, temp=%.1f°C}", 
                id, name, status, powerOutput, temperature);
    }
} 