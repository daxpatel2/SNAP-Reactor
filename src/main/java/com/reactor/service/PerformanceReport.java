package com.reactor.service;

import java.time.LocalDateTime;

/**
 * Represents a performance analysis report for a nuclear reactor.
 * Contains various performance metrics and operational data.
 */
public class PerformanceReport {
    private String reactorId;
    private LocalDateTime reportTime;
    private double currentPower;
    private double currentEfficiency;
    private double fuelLevel;
    private int operationalHours;
    private double averageControlRodInsertion;
    private double powerDensity;
    
    // Getters and Setters
    public String getReactorId() { return reactorId; }
    public void setReactorId(String reactorId) { this.reactorId = reactorId; }
    
    public LocalDateTime getReportTime() { return reportTime; }
    public void setReportTime(LocalDateTime reportTime) { this.reportTime = reportTime; }
    
    public double getCurrentPower() { return currentPower; }
    public void setCurrentPower(double currentPower) { this.currentPower = currentPower; }
    
    public double getCurrentEfficiency() { return currentEfficiency; }
    public void setCurrentEfficiency(double currentEfficiency) { this.currentEfficiency = currentEfficiency; }
    
    public double getFuelLevel() { return fuelLevel; }
    public void setFuelLevel(double fuelLevel) { this.fuelLevel = fuelLevel; }
    
    public int getOperationalHours() { return operationalHours; }
    public void setOperationalHours(int operationalHours) { this.operationalHours = operationalHours; }
    
    public double getAverageControlRodInsertion() { return averageControlRodInsertion; }
    public void setAverageControlRodInsertion(double averageControlRodInsertion) { 
        this.averageControlRodInsertion = averageControlRodInsertion; 
    }
    
    public double getPowerDensity() { return powerDensity; }
    public void setPowerDensity(double powerDensity) { this.powerDensity = powerDensity; }
    
    // Business Logic Methods
    public boolean isOperatingAtOptimalPower() {
        return currentPower >= 800.0 && currentPower <= 1100.0;
    }
    
    public boolean isOperatingAtOptimalEfficiency() {
        return currentEfficiency >= 80.0;
    }
    
    public double getPowerUtilization() {
        return (currentPower / 1200.0) * 100.0; // 1200 MW is max theoretical power
    }
    
    public boolean needsFuelRefill() {
        return fuelLevel < 20.0;
    }
    
    public boolean isControlRodInsertionOptimal() {
        return averageControlRodInsertion >= 20.0 && averageControlRodInsertion <= 80.0;
    }
    
    public String getPerformanceGrade() {
        double score = 0.0;
        
        if (isOperatingAtOptimalPower()) score += 25.0;
        if (isOperatingAtOptimalEfficiency()) score += 25.0;
        if (!needsFuelRefill()) score += 25.0;
        if (isControlRodInsertionOptimal()) score += 25.0;
        
        if (score >= 90.0) return "A";
        if (score >= 80.0) return "B";
        if (score >= 70.0) return "C";
        if (score >= 60.0) return "D";
        return "F";
    }
    
    @Override
    public String toString() {
        return String.format("PerformanceReport{reactorId='%s', power=%.1f MW, efficiency=%.1f%%, grade=%s}", 
                reactorId, currentPower, currentEfficiency, getPerformanceGrade());
    }
} 