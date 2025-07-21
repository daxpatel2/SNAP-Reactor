package com.reactor.service;

import com.reactor.model.Reactor;
import com.reactor.model.ControlRod;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for monitoring and analyzing reactor operations.
 * Provides various methods for reactor analysis and safety monitoring.
 */
public class ReactorMonitorService {
    
    /**
     * Analyzes the overall health status of a reactor.
     * @param reactor the reactor to analyze
     * @return a health status report
     */
    public ReactorHealthReport analyzeHealth(Reactor reactor) {
        ReactorHealthReport report = new ReactorHealthReport();
        report.setReactorId(reactor.getId());
        report.setAnalysisTime(LocalDateTime.now());
        
        // Temperature analysis
        if (reactor.getTemperature() > 500) {
            report.addWarning("High temperature detected: " + reactor.getTemperature() + "°C");
        } else if (reactor.getTemperature() < 50 && reactor.isOperational()) {
            report.addWarning("Low temperature for operational reactor: " + reactor.getTemperature() + "°C");
        }
        
        // Pressure analysis
        if (reactor.getPressure() > 20) {
            report.addWarning("High pressure detected: " + reactor.getPressure() + " MPa");
        }
        
        // Fuel level analysis
        if (reactor.getFuelLevel() < 10) {
            report.addWarning("Low fuel level: " + reactor.getFuelLevel() + "%");
        }
        
        // Control rod analysis
        List<ControlRod> controlRods = reactor.getControlRods();
        long nonOperationalRods = controlRods.stream()
                .filter(rod -> !rod.isOperational())
                .count();
        
        if (nonOperationalRods > 0) {
            report.addWarning(nonOperationalRods + " control rods are non-operational");
        }
        
        // Efficiency analysis
        double efficiency = reactor.getEfficiency();
        if (efficiency < 50 && reactor.isOperational()) {
            report.addWarning("Low efficiency: " + efficiency + "%");
        }
        
        // Maintenance analysis
        long daysSinceMaintenance = ChronoUnit.DAYS.between(
                reactor.getLastMaintenance(), LocalDateTime.now());
        if (daysSinceMaintenance > 365) {
            report.addWarning("Maintenance overdue by " + (daysSinceMaintenance - 365) + " days");
        }
        
        report.setHealthScore(calculateHealthScore(reactor));
        return report;
    }
    
    /**
     * Calculates a health score for the reactor (0-100).
     * @param reactor the reactor to score
     * @return health score between 0 and 100
     */
    private double calculateHealthScore(Reactor reactor) {
        double score = 100.0;
        
        // Temperature penalty
        if (reactor.getTemperature() > 400) {
            score -= (reactor.getTemperature() - 400) * 0.5;
        }
        
        // Pressure penalty
        if (reactor.getPressure() > 15) {
            score -= (reactor.getPressure() - 15) * 2.0;
        }
        
        // Fuel level penalty
        score -= (100 - reactor.getFuelLevel()) * 0.3;
        
        // Control rod penalty
        long nonOperationalRods = reactor.getControlRods().stream()
                .filter(rod -> !rod.isOperational())
                .count();
        score -= nonOperationalRods * 5.0;
        
        // Efficiency penalty
        if (reactor.isOperational()) {
            double efficiency = reactor.getEfficiency();
            score -= (100 - efficiency) * 0.2;
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * Checks if the reactor is operating within safe parameters.
     * @param reactor the reactor to check
     * @return true if safe, false otherwise
     */
    public boolean isOperatingSafely(Reactor reactor) {
        if (reactor.isInDangerZone()) {
            return false;
        }
        
        // Check control rods
        List<ControlRod> controlRods = reactor.getControlRods();
        long operationalRods = controlRods.stream()
                .filter(ControlRod::isOperational)
                .count();
        
        // Need at least 8 out of 10 control rods operational
        if (operationalRods < 8) {
            return false;
        }
        
        // Check if any control rods are fully withdrawn when reactor is operational
        if (reactor.isOperational()) {
            boolean hasFullyWithdrawnRods = controlRods.stream()
                    .anyMatch(ControlRod::isFullyWithdrawn);
            if (hasFullyWithdrawnRods) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Generates a performance report for the reactor.
     * @param reactor the reactor to analyze
     * @return performance report
     */
    public PerformanceReport generatePerformanceReport(Reactor reactor) {
        PerformanceReport report = new PerformanceReport();
        report.setReactorId(reactor.getId());
        report.setReportTime(LocalDateTime.now());
        
        report.setCurrentPower(reactor.getPowerOutput());
        report.setCurrentEfficiency(reactor.getEfficiency());
        report.setFuelLevel(reactor.getFuelLevel());
        report.setOperationalHours(reactor.getOperationalHours());
        
        // Calculate average control rod insertion
        double avgInsertion = reactor.getControlRods().stream()
                .mapToDouble(ControlRod::getInsertionLevel)
                .average()
                .orElse(0.0);
        report.setAverageControlRodInsertion(avgInsertion);
        
        // Calculate power density (power per unit volume - simplified)
        double powerDensity = reactor.getPowerOutput() / 1000.0; // MW/m³ (simplified)
        report.setPowerDensity(powerDensity);
        
        return report;
    }
    
    /**
     * Analyzes control rod effectiveness.
     * @param reactor the reactor to analyze
     * @return map of control rod IDs to their effectiveness
     */
    public Map<String, Double> analyzeControlRodEffectiveness(Reactor reactor) {
        return reactor.getControlRods().stream()
                .collect(Collectors.toMap(
                        ControlRod::getId,
                        ControlRod::getEffectiveness
                ));
    }
    
    /**
     * Predicts remaining operational time based on current fuel consumption.
     * @param reactor the reactor to analyze
     * @return predicted hours of operation remaining
     */
    public double predictRemainingOperationalTime(Reactor reactor) {
        if (reactor.getPowerOutput() == 0) {
            return Double.POSITIVE_INFINITY;
        }
        
        double fuelConsumptionRate = reactor.getPowerOutput() / 1000.0 * 0.1; // per hour
        return reactor.getFuelLevel() / fuelConsumptionRate;
    }
    
    /**
     * Checks if maintenance is recommended.
     * @param reactor the reactor to check
     * @return true if maintenance is recommended
     */
    public boolean isMaintenanceRecommended(Reactor reactor) {
        // Check fuel level
        if (reactor.getFuelLevel() < 15) {
            return true;
        }
        
        // Check time since last maintenance
        long daysSinceMaintenance = ChronoUnit.DAYS.between(
                reactor.getLastMaintenance(), LocalDateTime.now());
        if (daysSinceMaintenance > 365) {
            return true;
        }
        
        // Check operational hours
        if (reactor.getOperationalHours() > 8000) {
            return true;
        }
        
        // Check control rod status
        long nonOperationalRods = reactor.getControlRods().stream()
                .filter(rod -> !rod.isOperational())
                .count();
        if (nonOperationalRods > 2) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Calculates the reactor's thermal efficiency.
     * @param reactor the reactor to analyze
     * @return thermal efficiency percentage
     */
    public double calculateThermalEfficiency(Reactor reactor) {
        if (reactor.getPowerOutput() == 0) {
            return 0.0;
        }
        
        // Simplified thermal efficiency calculation
        // In a real reactor, this would be more complex
        double theoreticalMaxPower = 1200.0; // MW
        double thermalEfficiency = (reactor.getPowerOutput() / theoreticalMaxPower) * 100.0;
        
        // Adjust for temperature and pressure conditions
        if (reactor.getTemperature() > 400) {
            thermalEfficiency *= 0.95; // Efficiency decreases at high temperatures
        }
        
        if (reactor.getPressure() > 18) {
            thermalEfficiency *= 0.98; // Slight efficiency decrease at high pressure
        }
        
        return Math.min(100.0, thermalEfficiency);
    }
} 