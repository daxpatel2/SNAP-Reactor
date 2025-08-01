package com.reactor.service;

import com.reactor.model.Reactor;
import com.reactor.model.ControlRod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test class for the ReactorMonitorService using JUnit 5 and AssertJ.
 * Demonstrates service testing techniques and patterns.
 */
@DisplayName("Reactor Monitor Service Tests")
class ReactorMonitorServiceTest {
    
    private ReactorMonitorService monitorService;
    private Reactor reactor;
    
    @BeforeEach
    void setUp() {
        monitorService = new ReactorMonitorService();
        reactor = new Reactor("R-001", "Test Reactor");
    }
    
    @Nested
    @DisplayName("Health Analysis Tests")
    class HealthAnalysisTests {
        
        @Test
        @DisplayName("Should analyze healthy reactor correctly")
        void shouldAnalyzeHealthyReactorCorrectly() {
            // Given - Healthy reactor
            reactor.startUp();
            reactor.reachOperational();
            
            // When
            ReactorHealthReport report = monitorService.analyzeHealth(reactor);
            
            // Then
            assertThat(report.getReactorId()).isEqualTo("R-001");
            assertThat(report.getAnalysisTime()).isNotNull();
            assertThat(report.getHealthScore()).isGreaterThan(80.0);
            assertThat(report.isHealthy()).isTrue();
            assertThat(report.isCritical()).isFalse();
            assertThat(report.hasWarnings()).isFalse();
        }
        
        @Test
        @DisplayName("Should detect high temperature warning")
        void shouldDetectHighTemperatureWarning() {
            // Given
            reactor.setTemperature(600.0);
            
            // When
            ReactorHealthReport report = monitorService.analyzeHealth(reactor);
            
            // Then
            assertThat(report.hasWarnings()).isTrue();
            assertThat(report.getWarnings())
                    .anyMatch(warning -> warning.contains("High temperature detected"));
            assertThat(report.getHealthScore()).isLessThan(80.0);
        }
        
        @Test
        @DisplayName("Should detect high pressure warning")
        void shouldDetectHighPressureWarning() {
            // Given
            reactor.setPressure(25.0);
            
            // When
            ReactorHealthReport report = monitorService.analyzeHealth(reactor);
            
            // Then
            assertThat(report.hasWarnings()).isTrue();
            assertThat(report.getWarnings())
                    .anyMatch(warning -> warning.contains("High pressure detected"));
        }
        
        @Test
        @DisplayName("Should detect low fuel level warning")
        void shouldDetectLowFuelLevelWarning() {
            // Given
            reactor.setFuelLevel(5.0);
            
            // When
            ReactorHealthReport report = monitorService.analyzeHealth(reactor);
            
            // Then
            assertThat(report.hasWarnings()).isTrue();
            assertThat(report.getWarnings())
                    .anyMatch(warning -> warning.contains("Low fuel level"));
        }
        
        @Test
        @DisplayName("Should detect non-operational control rods warning")
        void shouldDetectNonOperationalControlRodsWarning() {
            // Given
            reactor.getControlRods().get(0).setOperational(false);
            reactor.getControlRods().get(1).setOperational(false);
            
            // When
            ReactorHealthReport report = monitorService.analyzeHealth(reactor);
            
            // Then
            assertThat(report.hasWarnings()).isTrue();
            assertThat(report.getWarnings())
                    .anyMatch(warning -> warning.contains("control rods are non-operational"));
        }
        
        @Test
        @DisplayName("Should detect low efficiency warning for operational reactor")
        void shouldDetectLowEfficiencyWarningForOperationalReactor() {
            // Given
            reactor.startUp();
            reactor.reachOperational();
            reactor.setFuelLevel(30.0); // This will reduce efficiency
            
            // When
            ReactorHealthReport report = monitorService.analyzeHealth(reactor);
            
            // Then
            assertThat(report.hasWarnings()).isTrue();
            assertThat(report.getWarnings())
                    .anyMatch(warning -> warning.contains("Low efficiency"));
        }
        
        @Test
        @DisplayName("Should calculate health score correctly")
        void shouldCalculateHealthScoreCorrectly() {
            // Given - Perfect reactor
            reactor.startUp();
            reactor.reachOperational();
            
            // When
            ReactorHealthReport report = monitorService.analyzeHealth(reactor);
            
            // Then
            assertThat(report.getHealthScore()).isCloseTo(100.0, within(5.0));
            
            // Given - Reactor with issues
            reactor.setTemperature(450.0);
            reactor.setPressure(18.0);
            reactor.setFuelLevel(50.0);
            
            // When
            report = monitorService.analyzeHealth(reactor);
            
            // Then
            assertThat(report.getHealthScore()).isLessThan(100.0);
            assertThat(report.getHealthScore()).isGreaterThan(0.0);
        }
    }
    
    @Nested
    @DisplayName("Safety Monitoring Tests")
    class SafetyMonitoringTests {
        
        @Test
        @DisplayName("Should identify safe operating conditions")
        void shouldIdentifySafeOperatingConditions() {
            // Given - Safe reactor
            reactor.startUp();
            reactor.reachOperational();
            
            // When
            boolean isSafe = monitorService.isOperatingSafely(reactor);
            
            // Then
            assertThat(isSafe).isTrue();
        }
        
        @Test
        @DisplayName("Should identify unsafe conditions due to danger zone")
        void shouldIdentifyUnsafeConditionsDueToDangerZone() {
            // Given - High temperature
            reactor.setTemperature(600.0);
            
            // When
            boolean isSafe = monitorService.isOperatingSafely(reactor);
            
            // Then
            assertThat(isSafe).isFalse();
        }
        
        @Test
        @DisplayName("Should identify unsafe conditions due to insufficient control rods")
        void shouldIdentifyUnsafeConditionsDueToInsufficientControlRods() {
            // Given - Too many non-operational control rods
            for (int i = 0; i < 4; i++) {
                reactor.getControlRods().get(i).setOperational(false);
            }
            
            // When
            boolean isSafe = monitorService.isOperatingSafely(reactor);
            
            // Then
            assertThat(isSafe).isFalse();
        }
        
        @Test
        @DisplayName("Should identify unsafe conditions due to fully withdrawn control rods")
        void shouldIdentifyUnsafeConditionsDueToFullyWithdrawnControlRods() {
            // Given - Operational reactor with fully withdrawn control rod
            reactor.startUp();
            reactor.reachOperational();
            reactor.getControlRods().get(0).setInsertionLevel(0.0);
            
            // When
            boolean isSafe = monitorService.isOperatingSafely(reactor);
            
            // Then
            assertThat(isSafe).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Performance Report Tests")
    class PerformanceReportTests {
        
        @BeforeEach
        void setUp() {
            reactor.startUp();
            reactor.reachOperational();
        }
        
        @Test
        @DisplayName("Should generate performance report correctly")
        void shouldGeneratePerformanceReportCorrectly() {
            // When
            PerformanceReport report = monitorService.generatePerformanceReport(reactor);
            
            // Then
            assertThat(report.getReactorId()).isEqualTo("R-001");
            assertThat(report.getReportTime()).isNotNull();
            assertThat(report.getCurrentPower()).isEqualTo(1000.0);
            assertThat(report.getCurrentEfficiency()).isEqualTo(100.0);
            assertThat(report.getFuelLevel()).isEqualTo(100.0);
            assertThat(report.getOperationalHours()).isEqualTo(0);
            assertThat(report.getAverageControlRodInsertion()).isEqualTo(100.0);
            assertThat(report.getPowerDensity()).isEqualTo(1.0);
        }
        
        @Test
        @DisplayName("Should calculate performance grade correctly")
        void shouldCalculatePerformanceGradeCorrectly() {
            // Given - Optimal conditions
            reactor.adjustPower(900.0); // Optimal power range

            // When
            PerformanceReport report = monitorService.generatePerformanceReport(reactor);
            
            // Then
            assertThat(report.getPerformanceGrade()).isEqualTo("C");
            
            // Given - Poor conditions
            reactor.adjustPower(500.0); // Low power
            reactor.setFuelLevel(15.0); // Low fuel
            
            // When
            report = monitorService.generatePerformanceReport(reactor);
            
            // Then
            assertThat(report.getPerformanceGrade()).isIn("C", "D", "F");
        }
        
        @Test
        @DisplayName("Should identify optimal power operation")
        void shouldIdentifyOptimalPowerOperation() {
            // Given
            reactor.adjustPower(900.0);
            
            // When
            PerformanceReport report = monitorService.generatePerformanceReport(reactor);
            
            // Then
            assertThat(report.isOperatingAtOptimalPower()).isTrue();
            
            // Given
            reactor.adjustPower(500.0);
            
            // When
            report = monitorService.generatePerformanceReport(reactor);
            
            // Then
            assertThat(report.isOperatingAtOptimalPower()).isFalse();
        }
        
        @Test
        @DisplayName("Should identify optimal efficiency operation")
        void shouldIdentifyOptimalEfficiencyOperation() {
            // Given
            reactor.setFuelLevel(90.0);
            
            // When
            PerformanceReport report = monitorService.generatePerformanceReport(reactor);
            
            // Then
            assertThat(report.isOperatingAtOptimalEfficiency()).isTrue();
            
            // Given
            reactor.setFuelLevel(50.0);
            
            // When
            report = monitorService.generatePerformanceReport(reactor);
            
            // Then
            assertThat(report.isOperatingAtOptimalEfficiency()).isFalse();
        }
        
        @Test
        @DisplayName("Should calculate power utilization correctly")
        void shouldCalculatePowerUtilizationCorrectly() {
            // Given
            reactor.adjustPower(600.0); // 600 MW out of 1200 MW max
            
            // When
            PerformanceReport report = monitorService.generatePerformanceReport(reactor);
            
            // Then
            assertThat(report.getPowerUtilization()).isEqualTo(50.0);
        }
        
        @Test
        @DisplayName("Should identify fuel refill needs")
        void shouldIdentifyFuelRefillNeeds() {
            // Given
            reactor.setFuelLevel(15.0);
            
            // When
            PerformanceReport report = monitorService.generatePerformanceReport(reactor);
            
            // Then
            assertThat(report.needsFuelRefill()).isTrue();
            
            // Given
            reactor.setFuelLevel(25.0);
            
            // When
            report = monitorService.generatePerformanceReport(reactor);
            
            // Then
            assertThat(report.needsFuelRefill()).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Control Rod Analysis Tests")
    class ControlRodAnalysisTests {
        
        @Test
        @DisplayName("Should analyze control rod effectiveness correctly")
        void shouldAnalyzeControlRodEffectivenessCorrectly() {
            // Given
            reactor.getControlRods().get(0).setInsertionLevel(75.0);
            reactor.getControlRods().get(1).setInsertionLevel(50.0);
            reactor.getControlRods().get(2).setOperational(false);
            
            // When
            Map<String, Double> effectiveness = monitorService.analyzeControlRodEffectiveness(reactor);
            
            // Then
            assertThat(effectiveness).hasSize(10);
            assertThat(effectiveness.get("CR-1")).isEqualTo(0.75);
            assertThat(effectiveness.get("CR-2")).isEqualTo(0.5);
            assertThat(effectiveness.get("CR-3")).isEqualTo(0.0);
        }
    }
    
    @Nested
    @DisplayName("Operational Time Prediction Tests")
    class OperationalTimePredictionTests {
        
        @Test
        @DisplayName("Should predict infinite time for shutdown reactor")
        void shouldPredictInfiniteTimeForShutdownReactor() {
            // Given - Shutdown reactor
            assertThat(reactor.getPowerOutput()).isEqualTo(0.0);
            
            // When
            double remainingTime = monitorService.predictRemainingOperationalTime(reactor);
            
            // Then
            assertThat(remainingTime).isEqualTo(Double.POSITIVE_INFINITY);
        }
        
        @Test
        @DisplayName("Should predict remaining operational time correctly")
        void shouldPredictRemainingOperationalTimeCorrectly() {
            // Given - Operational reactor with full fuel
            reactor.startUp();
            reactor.reachOperational();
            reactor.setFuelLevel(50.0); // 50% fuel remaining
            
            // When
            double remainingTime = monitorService.predictRemainingOperationalTime(reactor);
            
            // Then
            assertThat(remainingTime).isGreaterThan(0.0);
            assertThat(remainingTime).isFinite();
        }
    }
    
    @Nested
    @DisplayName("Maintenance Recommendation Tests")
    class MaintenanceRecommendationTests {
        
        @Test
        @DisplayName("Should recommend maintenance for low fuel level")
        void shouldRecommendMaintenanceForLowFuelLevel() {
            // Given
            reactor.setFuelLevel(10.0);
            
            // When
            boolean maintenanceRecommended = monitorService.isMaintenanceRecommended(reactor);
            
            // Then
            assertThat(maintenanceRecommended).isTrue();
        }
        
        @Test
        @DisplayName("Should recommend maintenance for overdue maintenance")
        void shouldRecommendMaintenanceForOverdueMaintenance() {
            // Given
            reactor.setLastMaintenance(LocalDateTime.now().minusDays(400));
            
            // When
            boolean maintenanceRecommended = monitorService.isMaintenanceRecommended(reactor);
            
            // Then
            assertThat(maintenanceRecommended).isTrue();
        }
        
        @Test
        @DisplayName("Should recommend maintenance for high operational hours")
        void shouldRecommendMaintenanceForHighOperationalHours() {
            // Given
            reactor.setOperationalHours(9000);
            
            // When
            boolean maintenanceRecommended = monitorService.isMaintenanceRecommended(reactor);
            
            // Then
            assertThat(maintenanceRecommended).isTrue();
        }
        
        @Test
        @DisplayName("Should recommend maintenance for too many non-operational control rods")
        void shouldRecommendMaintenanceForTooManyNonOperationalControlRods() {
            // Given
            for (int i = 0; i < 3; i++) {
                reactor.getControlRods().get(i).setOperational(false);
            }
            
            // When
            boolean maintenanceRecommended = monitorService.isMaintenanceRecommended(reactor);
            
            // Then
            assertThat(maintenanceRecommended).isTrue();
        }
        
        @Test
        @DisplayName("Should not recommend maintenance for healthy reactor")
        void shouldNotRecommendMaintenanceForHealthyReactor() {
            // Given - Healthy reactor
            reactor.setFuelLevel(80.0);
            reactor.setLastMaintenance(LocalDateTime.now().minusDays(200));
            reactor.setOperationalHours(5000);
            
            // When
            boolean maintenanceRecommended = monitorService.isMaintenanceRecommended(reactor);
            
            // Then
            assertThat(maintenanceRecommended).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Thermal Efficiency Tests")
    class ThermalEfficiencyTests {
        
        @Test
        @DisplayName("Should calculate thermal efficiency for operational reactor")
        void shouldCalculateThermalEfficiencyForOperationalReactor() {
            // Given
            reactor.startUp();
            reactor.reachOperational();
            
            // When
            double efficiency = monitorService.calculateThermalEfficiency(reactor);
            
            // Then
            assertThat(efficiency).isGreaterThan(0.0);
            assertThat(efficiency).isLessThanOrEqualTo(100.0);
        }
        
        @Test
        @DisplayName("Should return zero efficiency for shutdown reactor")
        void shouldReturnZeroEfficiencyForShutdownReactor() {
            // Given - Shutdown reactor
            assertThat(reactor.getPowerOutput()).isEqualTo(0.0);
            
            // When
            double efficiency = monitorService.calculateThermalEfficiency(reactor);
            
            // Then
            assertThat(efficiency).isEqualTo(0.0);
        }
        
        @ParameterizedTest
        @ValueSource(doubles = {500.0, 600.0})
        @DisplayName("Should reduce efficiency for high temperature")
        void shouldReduceEfficiencyForHighTemperature(double temperature) {
            // Given
            reactor.startUp();
            reactor.reachOperational();
            reactor.setTemperature(temperature);
            
            // When
            double efficiency = monitorService.calculateThermalEfficiency(reactor);
            
            // Then
            assertThat(efficiency).isLessThan(100.0);
        }
        
        @Test
        @DisplayName("Should reduce efficiency for high pressure")
        void shouldReduceEfficiencyForHighPressure() {
            // Given
            reactor.startUp();
            reactor.reachOperational();
            reactor.setPressure(20.0);
            
            // When
            double efficiency = monitorService.calculateThermalEfficiency(reactor);
            
            // Then
            assertThat(efficiency).isLessThan(100.0);
        }
    }
} 