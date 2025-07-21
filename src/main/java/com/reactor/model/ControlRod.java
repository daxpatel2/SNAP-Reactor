package com.reactor.model;

import java.util.Objects;

/**
 * Represents a control rod used in nuclear reactors to control the nuclear reaction.
 * Control rods can be inserted or withdrawn to adjust the reactor's power output.
 */
public class ControlRod {
    private String id;
    private double insertionLevel; // 0 = fully withdrawn, 100 = fully inserted
    private boolean isOperational;
    private double maxInsertionSpeed; // mm per second
    private double currentInsertionSpeed;
    
    public ControlRod(String id, double insertionLevel) {
        this.id = id;
        this.insertionLevel = Math.max(0, Math.min(100, insertionLevel));
        this.isOperational = true;
        this.maxInsertionSpeed = 10.0; // 10 mm per second
        this.currentInsertionSpeed = 0.0;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public double getInsertionLevel() { return insertionLevel; }
    public void setInsertionLevel(double insertionLevel) { 
        if (insertionLevel < 0 || insertionLevel > 100) {
            throw new IllegalArgumentException("Insertion level must be between 0 and 100");
        }
        this.insertionLevel = insertionLevel; 
    }
    
    public boolean isOperational() { return isOperational; }
    public void setOperational(boolean operational) { isOperational = operational; }
    
    public double getMaxInsertionSpeed() { return maxInsertionSpeed; }
    public void setMaxInsertionSpeed(double maxInsertionSpeed) { 
        if (maxInsertionSpeed < 0) {
            throw new IllegalArgumentException("Max insertion speed cannot be negative");
        }
        this.maxInsertionSpeed = maxInsertionSpeed; 
    }
    
    public double getCurrentInsertionSpeed() { return currentInsertionSpeed; }
    public void setCurrentInsertionSpeed(double currentInsertionSpeed) { 
        this.currentInsertionSpeed = currentInsertionSpeed; 
    }
    
    // Business Logic Methods
    public void insert(double amount) {
        if (!isOperational) {
            throw new IllegalStateException("Control rod is not operational");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Insertion amount cannot be negative");
        }
        
        insertionLevel = Math.min(100, insertionLevel + amount);
    }
    
    public void withdraw(double amount) {
        if (!isOperational) {
            throw new IllegalStateException("Control rod is not operational");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Withdrawal amount cannot be negative");
        }
        
        insertionLevel = Math.max(0, insertionLevel - amount);
    }
    
    public void fullyInsert() {
        if (!isOperational) {
            throw new IllegalStateException("Control rod is not operational");
        }
        insertionLevel = 100.0;
    }
    
    public void fullyWithdraw() {
        if (!isOperational) {
            throw new IllegalStateException("Control rod is not operational");
        }
        insertionLevel = 0.0;
    }
    
    public boolean isFullyInserted() {
        return insertionLevel >= 100.0;
    }
    
    public boolean isFullyWithdrawn() {
        return insertionLevel <= 0.0;
    }
    
    public double getEffectiveness() {
        if (!isOperational) return 0.0;
        return insertionLevel / 100.0;
    }
    
    public void simulateMovement(double seconds) {
        if (!isOperational || currentInsertionSpeed == 0) return;
        
        double movement = currentInsertionSpeed * seconds;
        if (currentInsertionSpeed > 0) {
            insert(movement);
        } else {
            withdraw(Math.abs(movement));
        }
    }
    
    public void setInsertionSpeed(double speed) {
        if (Math.abs(speed) > maxInsertionSpeed) {
            throw new IllegalArgumentException("Speed exceeds maximum insertion speed");
        }
        currentInsertionSpeed = speed;
    }
    
    public void emergencyInsert() {
        if (!isOperational) {
            throw new IllegalStateException("Control rod is not operational");
        }
        currentInsertionSpeed = maxInsertionSpeed;
        fullyInsert();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlRod that = (ControlRod) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("ControlRod{id='%s', insertion=%.1f%%, operational=%s}", 
                id, insertionLevel, isOperational);
    }
} 