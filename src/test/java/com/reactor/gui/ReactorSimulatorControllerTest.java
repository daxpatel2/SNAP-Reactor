package com.reactor.gui;

import com.reactor.model.Reactor;
import com.reactor.model.ControlRod;
import com.reactor.service.ReactorMonitorService;
import com.reactor.service.ReactorHealthReport;
import com.reactor.service.PerformanceReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.clearInvocations;

import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive test class for the ReactorSimulatorApp Swing application using JUnit 5, AssertJ, and Mockito.
 * Demonstrates GUI testing techniques including mocking and UI component testing.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Reactor Simulator App Tests")
@Timeout(value = 10, unit = TimeUnit.SECONDS)
class ReactorSimulatorControllerTest {

    private ReactorSimulatorApp app;

    @Mock private Reactor mockReactor;
    @Mock private ReactorMonitorService mockMonitorService;
    @Mock private ReactorHealthReport mockHealthReport;
    @Mock private PerformanceReport mockPerformanceReport;

    @BeforeEach
    void setUp() throws InterruptedException, InvocationTargetException {
        when(mockMonitorService.analyzeHealth(mockReactor)).thenReturn(mockHealthReport);
        when(mockHealthReport.getHealthScore()).thenReturn(99.0);
        lenient().when(mockMonitorService.generatePerformanceReport(mockReactor)).thenReturn(mockPerformanceReport);
        lenient().when(mockPerformanceReport.getCurrentPower()).thenReturn(99.0);


        // Create the Swing application
        SwingUtilities.invokeAndWait(() -> {
            app = new ReactorSimulatorApp(new Reactor("1","3 Mile Island"), new ReactorMonitorService());
            // Mock the reactor and services
            app.reactor = mockReactor;
            app.monitorService = mockMonitorService;
        });

        // Wait for Swing to initialize
        try {
            Thread.sleep(100);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Nested
    @DisplayName("Application Creation Tests")
    class ApplicationCreationTests {

        @Test
        @DisplayName("Assert that app is not null")
        void assertApplicationIsNotNull() {
            assertThat(app).isNotNull();
        }

        @Test
        @DisplayName("Should create Swing application successfully")
        void shouldCreateSwingApplicationSuccessfully() {

            assertThat(app).isInstanceOf(JFrame.class);
            assertThat(app.getTitle()).isEqualTo("Nuclear Reactor Simulator - Interactive Controls");
            assertThatCode(() -> app.setVisible(true)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should have main method for launching")
        void shouldHaveMainMethodForLaunching() {
            // Given
            String[] args = {"test"};

            // When & Then - This test just verifies the main method exists and doesn't throw
            assertThatCode(() -> ReactorSimulatorApp.main(args))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Reactor Control Tests")
    class ReactorControlTests {

        @Test
        @DisplayName("Should handle startup successfully")
        void shouldHandleStartupSuccessfully() {
            // Given
            doNothing().when(mockReactor).startUp();

            try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                mockedPane.when(() ->
                        JOptionPane.showMessageDialog(any(Component.class), anyString())
                ).then(invocation -> {
                    System.out.println("Mocked dialog shown");
                    return null; // Simulate pressing OK
                });

                // When
                app.handleStartUp();
            }

            // Then
            verify(mockReactor, times(1)).startUp();
        }

        @Test
        @DisplayName("Should handle startup failure")
        void shouldHandleStartupFailure() {

            // Given
            String errorMessage = "Insufficient fuel level for startup";
            doThrow(new IllegalStateException(errorMessage)).when(mockReactor).startUp();

            try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                mockedPane.when(() ->
                        JOptionPane.showMessageDialog(any(Component.class), anyString())
                ).then(invocation -> {
                    System.out.println("Mocked dialog shown");
                    return null; // Simulate pressing OK
                });

                // When
                app.handleStartUp();
            }
            // Then
            verify(mockReactor, times(1)).startUp();
        }

        @Test
        @DisplayName("Should handle shutdown successfully")
        void shouldHandleShutdownSuccessfully() {
            // Given
            doNothing().when(mockReactor).shutdown();

            try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                mockedPane.when(() ->
                        JOptionPane.showMessageDialog(any(Component.class), anyString())
                ).then(invocation -> {
                    System.out.println("Mocked dialog shown");
                    return null; // Simulate pressing OK
                });

                // When
                app.handleShutdown();
            }

            // Then
            verify(mockReactor, times(1)).shutdown();
        }

        @Test
        @DisplayName("Should handle emergency shutdown")
        void shouldHandleEmergencyShutdown() {
            // Given
            doNothing().when(mockReactor).emergencyShutdown();

            try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                mockedPane.when(() ->
                        JOptionPane.showMessageDialog(any(Component.class), anyString())
                ).then(invocation -> {
                    System.out.println("Mocked dialog shown");
                    return null; // Simulate pressing OK
                });

                // When
                app.handleEmergencyShutdown();
            }


            // Then
            verify(mockReactor, times(1)).emergencyShutdown();
        }

        @Test
        @DisplayName("Should handle power adjustment successfully")
        void shouldHandlePowerAdjustmentSuccessfully() {
            // Given
            double targetPower = 800.0;
            doNothing().when(mockReactor).adjustPower(anyDouble());

            try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                mockedPane.when(() ->
                        JOptionPane.showMessageDialog(any(Component.class), anyString())
                ).then(invocation -> {
                    System.out.println("Mocked dialog shown");
                    return null; // Simulate pressing OK
                });

                // When
                app.handleAdjustPower();
            }

            // Then
//            verify(mockReactor, times(1)).adjustPower(targetPower);
        }

        @Test
        @DisplayName("Should handle power adjustment failure")
        void shouldHandlePowerAdjustmentFailure() {
            // Given
            String errorMessage = "Power must be between 0 and 1200 MW";
            doThrow(new IllegalArgumentException(errorMessage)).when(mockReactor).adjustPower(anyDouble());

            try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                mockedPane.when(() ->
                        JOptionPane.showMessageDialog(any(Component.class), anyString())
                ).then(invocation -> {
                    System.out.println("Mocked dialog shown");
                    return null; // Simulate pressing OK
                });

                // When
                app.handleAdjustPower();
            }

            // Then
            verify(mockReactor, times(1)).adjustPower(anyDouble());
        }

        @Test
        @DisplayName("Should handle maintenance successfully")
        void shouldHandleMaintenanceSuccessfully() {
            // Given
            doNothing().when(mockReactor).performMaintenance();

            try (MockedStatic<JOptionPane> mockedPane = mockStatic(JOptionPane.class)) {
                mockedPane.when(() ->
                        JOptionPane.showMessageDialog(any(Component.class), anyString())
                ).then(invocation -> {
                    System.out.println("Mocked dialog shown");
                    return null; // Simulate pressing OK
                });

                // When
                app.handleMaintenance();
            }

            // Then
            verify(mockReactor, times(1)).performMaintenance();
        }
    }

    @Nested
    @DisplayName("Mocking Examples")
    class MockingExamples {

        @Test
        @DisplayName("Should demonstrate spy usage")
        void shouldDemonstrateSpyUsage() {
            // Given
            Reactor realReactor = new Reactor("R-001", "Test Reactor");
            Reactor spyReactor = spy(realReactor);

            // When
            doReturn(Reactor.ReactorStatus.OPERATIONAL).when(spyReactor).getStatus();
            doReturn(500.0).when(spyReactor).getTemperature();

            // Then
            assertThat(spyReactor.getStatus()).isEqualTo(Reactor.ReactorStatus.OPERATIONAL);
            assertThat(spyReactor.getTemperature()).isEqualTo(500.0);

            // Verify the real methods were called
            verify(spyReactor).getStatus();
            verify(spyReactor).getTemperature();
        }

        @Test
        @DisplayName("Should demonstrate argument matchers")
        void shouldDemonstrateArgumentMatchers() {
            // Given
            doNothing().when(mockReactor).adjustPower(anyDouble());
            doNothing().when(mockReactor).setTemperature(anyDouble());
            doNothing().when(mockReactor).setFuelLevel(anyDouble());

            // When
            mockReactor.adjustPower(800.0);
            mockReactor.setTemperature(300.0);
            mockReactor.setFuelLevel(50.0);

            // Then
            verify(mockReactor).adjustPower(800.0);
            verify(mockReactor).setTemperature(300.0);
            verify(mockReactor).setFuelLevel(50.0);
        }

        @Test
        @DisplayName("Should demonstrate exception testing with mocks")
        void shouldDemonstrateExceptionTestingWithMocks() {
            // Given
            doThrow(new IllegalStateException("Test exception")).when(mockReactor).startUp();

            // When & Then
            assertThatThrownBy(() -> mockReactor.startUp())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Test exception");
        }

        @Test
        @DisplayName("Should demonstrate multiple mock interactions")
        void shouldDemonstrateMultipleMockInteractions() {
            // Given
            when(mockReactor.getStatus()).thenReturn(Reactor.ReactorStatus.OPERATIONAL);
            when(mockReactor.getTemperature()).thenReturn(350.0);
            when(mockReactor.getPressure()).thenReturn(16.5);
            when(mockReactor.getPowerOutput()).thenReturn(950.0);

            when(mockMonitorService.analyzeHealth(mockReactor)).thenReturn(mockHealthReport);
            when(mockHealthReport.getHealthScore()).thenReturn(92.5);

            // When
            Reactor.ReactorStatus status = mockReactor.getStatus();
            double temperature = mockReactor.getTemperature();
            double pressure = mockReactor.getPressure();
            double power = mockReactor.getPowerOutput();
            ReactorHealthReport healthReport = mockMonitorService.analyzeHealth(mockReactor);
            double healthScore = healthReport.getHealthScore();

            // Then
            assertThat(status).isEqualTo(Reactor.ReactorStatus.OPERATIONAL);
            assertThat(temperature).isEqualTo(350.0);
            assertThat(pressure).isEqualTo(16.5);
            assertThat(power).isEqualTo(950.0);
            assertThat(healthScore).isEqualTo(92.5);
        }

        @Test
        @DisplayName("Should demonstrate collection mocking")
        void shouldDemonstrateCollectionMocking() {
            // Given
            ControlRod mockRod1 = mock(ControlRod.class);
            ControlRod mockRod2 = mock(ControlRod.class);

            when(mockRod1.getId()).thenReturn("CR-1");
            when(mockRod1.getInsertionLevel()).thenReturn(75.0);
            when(mockRod1.isOperational()).thenReturn(true);
            when(mockRod1.getEffectiveness()).thenReturn(0.75);

            when(mockRod2.getId()).thenReturn("CR-2");
            when(mockRod2.getInsertionLevel()).thenReturn(50.0);
            when(mockRod2.isOperational()).thenReturn(false);
            when(mockRod2.getEffectiveness()).thenReturn(0.0);

            when(mockReactor.getControlRods()).thenReturn(Arrays.asList(mockRod1, mockRod2));

            // When
            var controlRods = mockReactor.getControlRods();

            // Then
            assertThat(controlRods).hasSize(2);
            assertThat(controlRods.get(0).getId()).isEqualTo("CR-1");
            assertThat(controlRods.get(0).getInsertionLevel()).isEqualTo(75.0);
            assertThat(controlRods.get(0).isOperational()).isTrue();
            assertThat(controlRods.get(0).getEffectiveness()).isEqualTo(0.75);

            assertThat(controlRods.get(1).getId()).isEqualTo("CR-2");
            assertThat(controlRods.get(1).getInsertionLevel()).isEqualTo(50.0);
            assertThat(controlRods.get(1).isOperational()).isFalse();
            assertThat(controlRods.get(1).getEffectiveness()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle complete reactor startup sequence")
        void shouldHandleCompleteReactorStartupSequence() {
            // Given
            Reactor realReactor = new Reactor("R-001", "Test Reactor");
            ReactorMonitorService realService = new ReactorMonitorService();

            ReactorSimulatorApp testApp = new ReactorSimulatorApp(new Reactor("1","3 Mile Island"), new ReactorMonitorService());
            testApp.reactor = realReactor;
            testApp.monitorService = realService;

            // When - Start up sequence
            realReactor.startUp();
            realReactor.reachOperational();

            // Then
            assertThat(realReactor.getStatus()).isEqualTo(Reactor.ReactorStatus.OPERATIONAL);

            assertThat(realReactor.getPowerOutput()).isEqualTo(1000.0);
            assertThat(realReactor.getTemperature()).isCloseTo(300.0, within(0.1));

            // Test monitoring service integration
            ReactorHealthReport healthReport = realService.analyzeHealth(realReactor);
            assertThat(healthReport.getHealthScore()).isGreaterThan(80.0);
            assertThat(realService.isOperatingSafely(realReactor)).isTrue();
        }

        @Test
        @DisplayName("Should handle emergency shutdown scenario")
        void shouldHandleEmergencyShutdownScenario() {
            // Given
            Reactor realReactor = new Reactor("R-001", "Test Reactor");
            realReactor.startUp();
            realReactor.reachOperational();
            realReactor.setTemperature(600.0); // Dangerous temperature

            // When
            realReactor.emergencyShutdown();

            // Then
            assertThat(realReactor.getStatus()).isEqualTo(Reactor.ReactorStatus.EMERGENCY_SHUTDOWN);
            assertThat(realReactor.getTemperature()).isEqualTo(25.0);
            assertThat(realReactor.getPowerOutput()).isEqualTo(0.0);

            // Verify all control rods are fully inserted
            assertThat(realReactor.getControlRods())
                    .allSatisfy(rod -> assertThat(rod.getInsertionLevel()).isEqualTo(100.0));
        }

        @Test
        @DisplayName("Should handle power adjustment and monitoring integration")
        void shouldHandlePowerAdjustmentAndMonitoringIntegration() {
            // Given
            Reactor realReactor = new Reactor("R-001", "Test Reactor");
            ReactorMonitorService realService = new ReactorMonitorService();

            realReactor.startUp();
            realReactor.reachOperational();

            // When
            realReactor.adjustPower(800.0);

            // Then
            assertThat(realReactor.getPowerOutput()).isEqualTo(800.0);
            assertThat(realReactor.getTemperature()).isEqualTo(Math.round(291.6666666)); // 25 + (800/1200)*400

            // Test monitoring integration
            PerformanceReport report = realService.generatePerformanceReport(realReactor);
            assertThat(report.getCurrentPower()).isEqualTo(800.0);
            assertThat(report.isOperatingAtOptimalPower()).isTrue();
        }
    }

    @Nested
    @DisplayName("Service Layer Mocking Tests")
    class ServiceLayerMockingTests {

        @Test
        @DisplayName("Should mock monitoring service health analysis")
        void shouldMockMonitoringServiceHealthAnalysis() {
            // Given
            when(mockMonitorService.analyzeHealth(mockReactor)).thenReturn(mockHealthReport);
            when(mockHealthReport.getHealthScore()).thenReturn(85.5);
            when(mockHealthReport.hasWarnings()).thenReturn(true);
            when(mockHealthReport.getWarnings()).thenReturn(List.of("High temperature detected"));
            when(mockMonitorService.isOperatingSafely(mockReactor)).thenReturn(false);

            // When
            ReactorHealthReport healthReport = mockMonitorService.analyzeHealth(mockReactor);
            boolean isSafe = mockMonitorService.isOperatingSafely(mockReactor);

            // Then
            assertThat(healthReport.getHealthScore()).isEqualTo(85.5);
            assertThat(healthReport.hasWarnings()).isTrue();
            assertThat(healthReport.getWarnings()).contains("High temperature detected");
            assertThat(isSafe).isFalse();

//            verify(mockMonitorService).analyzeHealth(mockReactor);
//            verify(mockMonitorService).isOperatingSafely(mockReactor);
//            verify(mockMonitorService, times(2)).analyzeHealth(mockReactor);

        }

        @Test
        @DisplayName("Should mock performance report generation")
        void shouldMockPerformanceReportGeneration() {
            // Given
            when(mockPerformanceReport.getCurrentPower()).thenReturn(900.0);
            when(mockPerformanceReport.getCurrentEfficiency()).thenReturn(90.0);
            when(mockPerformanceReport.getPerformanceGrade()).thenReturn("A");
            when(mockPerformanceReport.isOperatingAtOptimalPower()).thenReturn(true);
            when(mockPerformanceReport.isOperatingAtOptimalEfficiency()).thenReturn(true);

            // When
            PerformanceReport report = mockMonitorService.generatePerformanceReport(mockReactor);

            // Then
            assertThat(report.getCurrentPower()).isEqualTo(900.0);
            assertThat(report.getCurrentEfficiency()).isEqualTo(90.0);
            assertThat(report.getPerformanceGrade()).isEqualTo("A");
            assertThat(report.isOperatingAtOptimalPower()).isTrue();
            assertThat(report.isOperatingAtOptimalEfficiency()).isTrue();

        }
    }
}