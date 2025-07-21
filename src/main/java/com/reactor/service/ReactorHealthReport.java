package com.reactor.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a health analysis report for a nuclear reactor.
 * Contains warnings, health score, and analysis time.
 */
public class ReactorHealthReport {
    private String reactorId;
    private LocalDateTime analysisTime;
    private double healthScore;
    private List<String> warnings;
    
    public ReactorHealthReport() {
        this.warnings = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getReactorId() { return reactorId; }
    public void setReactorId(String reactorId) { this.reactorId = reactorId; }
    
    public LocalDateTime getAnalysisTime() { return analysisTime; }
    public void setAnalysisTime(LocalDateTime analysisTime) { this.analysisTime = analysisTime; }
    
    public double getHealthScore() { return healthScore; }
    public void setHealthScore(double healthScore) { this.healthScore = healthScore; }
    
    public List<String> getWarnings() { return new ArrayList<>(warnings); }
    
    public void addWarning(String warning) {
        this.warnings.add(warning);
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public int getWarningCount() {
        return warnings.size();
    }
    
    public boolean isHealthy() {
        return healthScore >= 80.0;
    }
    
    public boolean isCritical() {
        return healthScore < 50.0;
    }
    
    @Override
    public String toString() {
        return String.format("ReactorHealthReport{reactorId='%s', healthScore=%.1f, warnings=%d}", 
                reactorId, healthScore, warnings.size());
    }
} 