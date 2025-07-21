package com.reactor.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for the Reactor model using JUnit 5 and AssertJ.
 * Demonstrates various testing techniques and patterns.
 */
@DisplayName("Reactor Tests")
class ReactorTest {
    
    private Reactor reactor;
    
    @BeforeEach
    void setUp() {
        reactor = new Reactor("R-001", "Test Reactor");
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create reactor with correct initial values")
        void shouldCreateReactorWithCorrectInitialValues() {
            // Given
            String id = "R-002";
            String name = "New Reactor";
            
            // When
            Reactor newReactor = new Reactor(id, name);
            
            // Then
            assertThat(newReactor.getId()).isEqualTo(id);
            assertThat(newReactor.getName()).isEqualTo(name);
            assertThat(newReactor.getTemperature()).isEqualTo(25.0);
            assertThat(newReactor.getPressure()).isEqualTo(0.1);
            assertThat(newReactor.getPowerOutput()).isEqualTo(0.0);
            assertThat(newReactor.getFuelLevel()).isEqualTo(100.0);
            assertThat(newReactor.getStatus()).isEqualTo(Reactor.ReactorStatus.SHUTDOWN);
            assertThat(newReactor.getOperationalHours()).isEqualTo(0);
        }
        
        @Test
        @DisplayName("Should initialize 10 control rods")
        void shouldInitializeTenControlRods() {
            // When
            List<ControlRod> controlRods = reactor.getControlRods();
            
            // Then
            assertThat(controlRods).hasSize(10);
            assertThat(controlRods)
                    .extracting(ControlRod::getId)
                    .containsExactlyInAnyOrder(
                            "CR-1", "CR-2", "CR-3", "CR-4", "CR-5",
                            "CR-6", "CR-7", "CR-8", "CR-9", "CR-10"
                    );
            
            assertThat(controlRods)
                    .allSatisfy(rod -> {
                        assertThat(rod.getInsertionLevel()).isEqualTo(100.0);
                        assertThat(rod.isOperational()).isTrue();
                    });
        }
    }
    
    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @Test
        @DisplayName("Should set and get temperature correctly")
        void shouldSetAndGetTemperatureCorrectly() {
            // When
            reactor.setTemperature(150.0);
            
            // Then
            assertThat(reactor.getTemperature()).isEqualTo(150.0);
        }
        
        @Test
        @DisplayName("Should throw exception for negative temperature")
        void shouldThrowExceptionForNegativeTemperature() {
            // Then
            assertThatThrownBy(() -> reactor.setTemperature(-10.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Temperature cannot be negative");
        }
        
        @Test
        @DisplayName("Should set and get pressure correctly")
        void shouldSetAndGetPressureCorrectly() {
            // When
            reactor.setPressure(15.5);
            
            // Then
            assertThat(reactor.getPressure()).isEqualTo(15.5);
        }
        
        @Test
        @DisplayName("Should throw exception for negative pressure")
        void shouldThrowExceptionForNegativePressure() {
            // Then
            assertThatThrownBy(() -> reactor.setPressure(-5.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Pressure cannot be negative");
        }
        
        @Test
        @DisplayName("Should set and get power output correctly")
        void shouldSetAndGetPowerOutputCorrectly() {
            // When
            reactor.setPowerOutput(800.0);
            
            // Then
            assertThat(reactor.getPowerOutput()).isEqualTo(800.0);
        }
        
        @Test
        @DisplayName("Should throw exception for negative power output")
        void shouldThrowExceptionForNegativePowerOutput() {
            // Then
            assertThatThrownBy(() -> reactor.setPowerOutput(-100.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Power output cannot be negative");
        }
        
        @ParameterizedTest
        @ValueSource(doubles = {0.0, 50.0, 100.0})
        @DisplayName("Should accept valid fuel levels")
        void shouldAcceptValidFuelLevels(double fuelLevel) {
            // When
            reactor.setFuelLevel(fuelLevel);
            
            // Then
            assertThat(reactor.getFuelLevel()).isEqualTo(fuelLevel);
        }
        
        @ParameterizedTest
        @ValueSource(doubles = {-10.0, 150.0})
        @DisplayName("Should throw exception for invalid fuel levels")
        void shouldThrowExceptionForInvalidFuelLevels(double fuelLevel) {
            // Then
            assertThatThrownBy(() -> reactor.setFuelLevel(fuelLevel))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Fuel level must be between 0 and 100");
        }
    }
    
    @Nested
    @DisplayName("Startup Sequence Tests")
    class StartupSequenceTests {
        
        @Test
        @DisplayName("Should start up successfully from shutdown status")
        void shouldStartUpSuccessfullyFromShutdownStatus() {
            // Given
            assertThat(reactor.getStatus()).isEqualTo(Reactor.ReactorStatus.SHUTDOWN);
            assertThat(reactor.getFuelLevel()).isEqualTo(100.0);
            
            // When
            reactor.startUp();
            
            // Then
            assertThat(reactor.getStatus()).isEqualTo(Reactor.ReactorStatus.STARTING_UP);
            assertThat(reactor.getTemperature()).isEqualTo(100.0);
            assertThat(reactor.getPressure()).isEqualTo(1.0);
        }
        
        @Test
        @DisplayName("Should start up successfully from maintenance status")
        void shouldStartUpSuccessfullyFromMaintenanceStatus() {
            // Given
            reactor.setStatus(Reactor.ReactorStatus.MAINTENANCE);
            reactor.setFuelLevel(50.0);
            
            // When
            reactor.startUp();
            
            // Then
            assertThat(reactor.getStatus()).isEqualTo(Reactor.ReactorStatus.STARTING_UP);
        }
        
        @Test
        @DisplayName("Should throw exception when starting up with insufficient fuel")
        void shouldThrowExceptionWhenStartingUpWithInsufficientFuel() {
            // Given
            reactor.setFuelLevel(5.0);
            
            // Then
            assertThatThrownBy(() -> reactor.startUp())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Insufficient fuel level for startup");
        }
        
        @Test
        @DisplayName("Should throw exception when starting up from operational status")
        void shouldThrowExceptionWhenStartingUpFromOperationalStatus() {
            // Given
            reactor.setStatus(Reactor.ReactorStatus.OPERATIONAL);
            
            // Then
            assertThatThrownBy(() -> reactor.startUp())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Reactor can only be started from SHUTDOWN or MAINTENANCE status");
        }
        
        @Test
        @DisplayName("Should reach operational status successfully")
        void shouldReachOperationalStatusSuccessfully() {
            // Given
            reactor.startUp();
            
            // When
            reactor.reachOperational();
            
            // Then
            assertThat(reactor.getStatus()).isEqualTo(Reactor.ReactorStatus.OPERATIONAL);
            assertThat(reactor.getTemperature()).isEqualTo(300.0);
            assertThat(reactor.getPressure()).isEqualTo(15.0);
            assertThat(reactor.getPowerOutput()).isEqualTo(1000.0);
        }
        
        @Test
        @DisplayName("Should throw exception when reaching operational from wrong status")
        void shouldThrowExceptionWhenReachingOperationalFromWrongStatus() {
            // Then
            assertThatThrownBy(() -> reactor.reachOperational())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Reactor must be in STARTING_UP status to reach operational");
        }
    }
    
    @Nested
    @DisplayName("Shutdown Tests")
    class ShutdownTests {
        
        @Test
        @DisplayName("Should shutdown successfully")
        void shouldShutdownSuccessfully() {
            // Given
            reactor.setStatus(Reactor.ReactorStatus.OPERATIONAL);
            reactor.setTemperature(400.0);
            reactor.setPressure(18.0);
            reactor.setPowerOutput(1000.0);
            
            // When
            reactor.shutdown();
            
            // Then
            assertThat(reactor.getStatus()).isEqualTo(Reactor.ReactorStatus.SHUTDOWN);
            assertThat(reactor.getTemperature()).isEqualTo(50.0);
            assertThat(reactor.getPressure()).isEqualTo(0.5);
            assertThat(reactor.getPowerOutput()).isEqualTo(0.0);
        }
        
        @Test
        @DisplayName("Should perform emergency shutdown")
        void shouldPerformEmergencyShutdown() {
            // Given
            reactor.setStatus(Reactor.ReactorStatus.OPERATIONAL);
            reactor.setTemperature(600.0);
            reactor.setPressure(25.0);
            reactor.setPowerOutput(1200.0);
            
            // When
            reactor.emergencyShutdown();
            
            // Then
            assertThat(reactor.getStatus()).isEqualTo(Reactor.ReactorStatus.EMERGENCY_SHUTDOWN);
            assertThat(reactor.getTemperature()).isEqualTo(25.0);
            assertThat(reactor.getPressure()).isEqualTo(0.1);
            assertThat(reactor.getPowerOutput()).isEqualTo(0.0);
            
            // Check that all control rods are fully inserted
            assertThat(reactor.getControlRods())
                    .allSatisfy(rod -> assertThat(rod.getInsertionLevel()).isEqualTo(100.0));
        }
        
        @Test
        @DisplayName("Should throw exception when shutting down from emergency shutdown")
        void shouldThrowExceptionWhenShuttingDownFromEmergencyShutdown() {
            // Given
            reactor.setStatus(Reactor.ReactorStatus.EMERGENCY_SHUTDOWN);
            
            // Then
            assertThatThrownBy(() -> reactor.shutdown())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Reactor is already in emergency shutdown");
        }
    }
    
    @Nested
    @DisplayName("Power Adjustment Tests")
    class PowerAdjustmentTests {
        
        @BeforeEach
        void setUp() {
            reactor.startUp();
            reactor.reachOperational();
        }
        
        @ParameterizedTest
        @CsvSource({
                "0.0, 300.0, 15.0",
                "500.0, 400.0, 17.5",
                "1000.0, 500.0, 20.0",
                "1200.0, 540.0, 21.0"
        })
        @DisplayName("Should adjust power and update temperature and pressure")
        void shouldAdjustPowerAndUpdateTemperatureAndPressure(double targetPower, double expectedTemp, double expectedPressure) {
            // When
            reactor.adjustPower(targetPower);
            
            // Then
            assertThat(reactor.getPowerOutput()).isEqualTo(targetPower);
            assertThat(reactor.getTemperature()).isEqualTo(expectedTemp);
            assertThat(reactor.getPressure()).isEqualTo(expectedPressure);
        }
        
        @ParameterizedTest
        @ValueSource(doubles = {-100.0, 1300.0})
        @DisplayName("Should throw exception for invalid power values")
        void shouldThrowExceptionForInvalidPowerValues(double invalidPower) {
            // Then
            assertThatThrownBy(() -> reactor.adjustPower(invalidPower))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Power must be between 0 and 1200 MW");
        }
        
        @Test
        @DisplayName("Should throw exception when adjusting power from non-operational status")
        void shouldThrowExceptionWhenAdjustingPowerFromNonOperationalStatus() {
            // Given
            reactor.shutdown();
            
            // Then
            assertThatThrownBy(() -> reactor.adjustPower(500.0))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Power can only be adjusted when reactor is operational");
        }
    }
    
    @Nested
    @DisplayName("Control Rod Tests")
    class ControlRodTests {
        
        @BeforeEach
        void setUp() {
            reactor.startUp();
            reactor.reachOperational();
        }
        
        @Test
        @DisplayName("Should insert control rod successfully")
        void shouldInsertControlRodSuccessfully() {
            // When
            reactor.insertControlRod("CR-1", 50.0);
            
            // Then
            ControlRod rod = reactor.getControlRods().stream()
                    .filter(r -> r.getId().equals("CR-1"))
                    .findFirst()
                    .orElseThrow();
            
            assertThat(rod.getInsertionLevel()).isEqualTo(50.0);
        }
        
        @Test
        @DisplayName("Should throw exception for non-existent control rod")
        void shouldThrowExceptionForNonExistentControlRod() {
            // Then
            assertThatThrownBy(() -> reactor.insertControlRod("CR-99", 50.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Control rod not found: CR-99");
        }
        
        @Test
        @DisplayName("Should reduce power when control rods are inserted")
        void shouldReducePowerWhenControlRodsAreInserted() {
            // Given
            double initialPower = reactor.getPowerOutput();
            
            // When
            reactor.insertControlRod("CR-1", 100.0);
            reactor.insertControlRod("CR-2", 100.0);
            reactor.insertControlRod("CR-3", 100.0);
            
            // Then
            assertThat(reactor.getPowerOutput()).isLessThan(initialPower);
        }
    }
    
    @Nested
    @DisplayName("Status and Safety Tests")
    class StatusAndSafetyTests {
        
        @Test
        @DisplayName("Should return correct operational status")
        void shouldReturnCorrectOperationalStatus() {
            // Given
            assertThat(reactor.isOperational()).isFalse();
            
            // When
            reactor.startUp();
            reactor.reachOperational();
            
            // Then
            assertThat(reactor.isOperational()).isTrue();
        }
        
        @Test
        @DisplayName("Should detect danger zone conditions")
        void shouldDetectDangerZoneConditions() {
            // Given - Normal conditions
            assertThat(reactor.isInDangerZone()).isFalse();
            
            // When - High temperature
            reactor.setTemperature(600.0);
            
            // Then
            assertThat(reactor.isInDangerZone()).isTrue();
            
            // When - High pressure
            reactor.setTemperature(25.0);
            reactor.setPressure(25.0);
            
            // Then
            assertThat(reactor.isInDangerZone()).isTrue();
            
            // When - Low fuel
            reactor.setPressure(0.1);
            reactor.setFuelLevel(3.0);
            
            // Then
            assertThat(reactor.isInDangerZone()).isTrue();
        }
        
        @Test
        @DisplayName("Should calculate efficiency correctly")
        void shouldCalculateEfficiencyCorrectly() {
            // Given - Shutdown reactor
            assertThat(reactor.getEfficiency()).isEqualTo(0.0);
            
            // When - Operational reactor at full power and fuel
            reactor.startUp();
            reactor.reachOperational();
            
            // Then
            assertThat(reactor.getEfficiency()).isEqualTo(100.0);
            
            // When - Reduced fuel level
            reactor.setFuelLevel(50.0);
            
            // Then
            assertThat(reactor.getEfficiency()).isEqualTo(50.0);
        }
    }
    
    @Nested
    @DisplayName("Fuel Consumption Tests")
    class FuelConsumptionTests {
        
        @BeforeEach
        void setUp() {
            reactor.startUp();
            reactor.reachOperational();
        }
        
        @Test
        @DisplayName("Should consume fuel correctly")
        void shouldConsumeFuelCorrectly() {
            // Given
            double initialFuel = reactor.getFuelLevel();
            double initialHours = reactor.getOperationalHours();
            
            // When
            reactor.consumeFuel(10.0);
            
            // Then
            assertThat(reactor.getFuelLevel()).isLessThan(initialFuel);
            assertThat(reactor.getOperationalHours()).isEqualTo((int) (initialHours + 10));
        }
        
        @Test
        @DisplayName("Should enter maintenance mode when fuel is low")
        void shouldEnterMaintenanceModeWhenFuelIsLow() {
            // Given
            reactor.setFuelLevel(10.0);
            
            // When
            reactor.consumeFuel(5.0);
            
            // Then
            assertThat(reactor.getStatus()).isEqualTo(Reactor.ReactorStatus.MAINTENANCE);
        }
        
        @Test
        @DisplayName("Should throw exception for negative fuel consumption time")
        void shouldThrowExceptionForNegativeFuelConsumptionTime() {
            // Then
            assertThatThrownBy(() -> reactor.consumeFuel(-5.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Hours cannot be negative");
        }
    }
    
    @Nested
    @DisplayName("Maintenance Tests")
    class MaintenanceTests {
        
        @Test
        @DisplayName("Should perform maintenance successfully")
        void shouldPerformMaintenanceSuccessfully() {
            // Given
            reactor.setStatus(Reactor.ReactorStatus.MAINTENANCE);
            reactor.setFuelLevel(5.0);
            LocalDateTime beforeMaintenance = reactor.getLastMaintenance();
            
            // When
            reactor.performMaintenance();
            
            // Then
            assertThat(reactor.getStatus()).isEqualTo(Reactor.ReactorStatus.SHUTDOWN);
            assertThat(reactor.getFuelLevel()).isEqualTo(100.0);
            assertThat(reactor.getLastMaintenance()).isAfter(beforeMaintenance);
        }
        
        @Test
        @DisplayName("Should throw exception when performing maintenance from wrong status")
        void shouldThrowExceptionWhenPerformingMaintenanceFromWrongStatus() {
            // Then
            assertThatThrownBy(() -> reactor.performMaintenance())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Maintenance can only be performed when reactor is in MAINTENANCE status");
        }
    }
    
    @Nested
    @DisplayName("Object Methods Tests")
    class ObjectMethodsTests {
        
        @Test
        @DisplayName("Should implement equals correctly")
        void shouldImplementEqualsCorrectly() {
            // Given
            Reactor reactor1 = new Reactor("R-001", "Reactor 1");
            Reactor reactor2 = new Reactor("R-001", "Reactor 2");
            Reactor reactor3 = new Reactor("R-002", "Reactor 1");
            
            // Then
            assertThat(reactor1).isEqualTo(reactor2);
            assertThat(reactor1).isNotEqualTo(reactor3);
            assertThat(reactor1).isNotEqualTo(null);
            assertThat(reactor1).isEqualTo(reactor1);
        }
        
        @Test
        @DisplayName("Should implement hashCode correctly")
        void shouldImplementHashCodeCorrectly() {
            // Given
            Reactor reactor1 = new Reactor("R-001", "Reactor 1");
            Reactor reactor2 = new Reactor("R-001", "Reactor 2");
            
            // Then
            assertThat(reactor1.hashCode()).isEqualTo(reactor2.hashCode());
        }
        
        @Test
        @DisplayName("Should implement toString correctly")
        void shouldImplementToStringCorrectly() {
            // Given
            Reactor reactor = new Reactor("R-001", "Test Reactor");
            
            // When
            String result = reactor.toString();
            
            // Then
            assertThat(result)
                    .contains("R-001")
                    .contains("Test Reactor")
                    .contains("SHUTDOWN")
                    .contains("0.0 MW")
                    .contains("25.0°C");
        }
    }
    
    @ParameterizedTest
    @MethodSource("operationalHoursProvider")
    @DisplayName("Should set operational hours correctly")
    void shouldSetOperationalHoursCorrectly(int hours, boolean shouldThrowException) {
        if (shouldThrowException) {
            assertThatThrownBy(() -> reactor.setOperationalHours(hours))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Operational hours cannot be negative");
        } else {
            reactor.setOperationalHours(hours);
            assertThat(reactor.getOperationalHours()).isEqualTo(hours);
        }
    }
    
    static Stream<Arguments> operationalHoursProvider() {
        return Stream.of(
                Arguments.of(0, false),
                Arguments.of(100, false),
                Arguments.of(1000, false),
                Arguments.of(-1, true),
                Arguments.of(-100, true)
        );
    }
}