package com.reactor.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test class for the ControlRod model using JUnit 5 and AssertJ.
 * Demonstrates various testing techniques and patterns.
 */
@DisplayName("Control Rod Tests")
class ControlRodTest {
    
    private ControlRod controlRod;

    // BeforeEach -> runs methods with these annotations before each test case is called
    @BeforeEach
    void setUp() {
        controlRod = new ControlRod("CR-001", 50.0);
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create control rod with correct initial values")
        void shouldCreateControlRodWithCorrectInitialValues() {
            // Given
            String id = "CR-002";
            double insertionLevel = 75.0;

//            String name = "PENN STATE NUCLEAR REACTOR";

            // When
            ControlRod newRod = new ControlRod(id, insertionLevel);
            
            // Then
            assertThat(newRod.getId()).isEqualTo(id);
            assertThat(newRod.getInsertionLevel()).isEqualTo(insertionLevel);
            assertThat(newRod.isOperational()).isTrue();
            assertThat(newRod.getMaxInsertionSpeed()).isEqualTo(10.0);
            assertThat(newRod.getCurrentInsertionSpeed()).isEqualTo(0.0);
//            assertThat(name).isEqualTo("PENN STATE NUCLEAR REACTOR");

            //with JUNIT IT WOULD LOOK LIKE
            assertEquals(id, newRod.getId());
            assertTrue(newRod.isOperational());

        }

        @Test
        @DisplayName("Should clamp insertion level to valid range")
        void shouldClampInsertionLevelToValidRange() {
            // When
            ControlRod rod1 = new ControlRod("CR-1", -10.0);
            ControlRod rod2 = new ControlRod("CR-2", 150.0);
            
            // Then
            assertThat(rod1.getInsertionLevel()).isEqualTo(0.0);
            assertThat(rod2.getInsertionLevel()).isEqualTo(100.0);
        }
    }
    
    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {
        
        @Test
        @DisplayName("Should set and get insertion level correctly")
        void shouldSetAndGetInsertionLevelCorrectly() {
            // When
            controlRod.setInsertionLevel(75.0);
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(75.0);
        }
        
        @ParameterizedTest
        @ValueSource(doubles = {0.0, 50.0, 100.0})
        @DisplayName("Should accept valid insertion levels")
        void shouldAcceptValidInsertionLevels(double insertionLevel) {
            // When
            controlRod.setInsertionLevel(insertionLevel);
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(insertionLevel);
        }
        
        @ParameterizedTest
        @ValueSource(doubles = {-10.0, 150.0})
        @DisplayName("Should throw exception for invalid insertion levels")
        void shouldThrowExceptionForInvalidInsertionLevels(double insertionLevel) {
            // Then
            assertThatThrownBy(() -> controlRod.setInsertionLevel(insertionLevel))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Insertion level must be between 0 and 100");
        }
        
        @Test
        @DisplayName("Should set and get operational status correctly")
        void shouldSetAndGetOperationalStatusCorrectly() {
            // When
            controlRod.setOperational(false);
            
            // Then
            assertThat(controlRod.isOperational()).isFalse();
            
            // When
            controlRod.setOperational(true);
            
            // Then
            assertThat(controlRod.isOperational()).isTrue();
        }
        
        @Test
        @DisplayName("Should set and get max insertion speed correctly")
        void shouldSetAndGetMaxInsertionSpeedCorrectly() {
            // When
            controlRod.setMaxInsertionSpeed(15.0);
            
            // Then
            assertThat(controlRod.getMaxInsertionSpeed()).isEqualTo(15.0);
        }
        
        @Test
        @DisplayName("Should throw exception for negative max insertion speed")
        void shouldThrowExceptionForNegativeMaxInsertionSpeed() {
            // Then
            assertThatThrownBy(() -> controlRod.setMaxInsertionSpeed(-5.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Max insertion speed cannot be negative");
        }
        
        @Test
        @DisplayName("Should set and get current insertion speed correctly")
        void shouldSetAndGetCurrentInsertionSpeedCorrectly() {
            // When
            controlRod.setCurrentInsertionSpeed(5.0);
            
            // Then
            assertThat(controlRod.getCurrentInsertionSpeed()).isEqualTo(5.0);
            
            // When
            controlRod.setCurrentInsertionSpeed(-3.0);
            
            // Then
            assertThat(controlRod.getCurrentInsertionSpeed()).isEqualTo(-3.0);
        }
    }
    
    @Nested
    @DisplayName("Insertion and Withdrawal Tests")
    class InsertionAndWithdrawalTests {
        
        @Test
        @DisplayName("Should insert control rod correctly")
        void shouldInsertControlRodCorrectly() {
            // Given
            double initialLevel = controlRod.getInsertionLevel();
            double insertAmount = 20.0;
            
            // When
            controlRod.insert(insertAmount);
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(initialLevel + insertAmount);
        }
        
        @Test
        @DisplayName("Should clamp insertion to maximum level")
        void shouldClampInsertionToMaximumLevel() {
            // Given
            controlRod.setInsertionLevel(90.0);
            
            // When
            controlRod.insert(20.0);
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(100.0);
        }
        
        @Test
        @DisplayName("Should throw exception for negative insertion amount")
        void shouldThrowExceptionForNegativeInsertionAmount() {
            // Then
            assertThatThrownBy(() -> controlRod.insert(-10.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Insertion amount cannot be negative");
        }
        
        @Test
        @DisplayName("Should throw exception when inserting non-operational rod")
        void shouldThrowExceptionWhenInsertingNonOperationalRod() {
            // Given
            controlRod.setOperational(false);
            
            // Then
            assertThatThrownBy(() -> controlRod.insert(10.0))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Control rod is not operational");
        }
        
        @Test
        @DisplayName("Should withdraw control rod correctly")
        void shouldWithdrawControlRodCorrectly() {
            // Given
            double initialLevel = controlRod.getInsertionLevel();
            double withdrawAmount = 20.0;
            
            // When
            controlRod.withdraw(withdrawAmount);
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(initialLevel - withdrawAmount);
        }
        
        @Test
        @DisplayName("Should clamp withdrawal to minimum level")
        void shouldClampWithdrawalToMinimumLevel() {
            // Given
            controlRod.setInsertionLevel(10.0);
            
            // When
            controlRod.withdraw(20.0);
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(0.0);
        }
        
        @Test
        @DisplayName("Should throw exception for negative withdrawal amount")
        void shouldThrowExceptionForNegativeWithdrawalAmount() {
            // Then
            assertThatThrownBy(() -> controlRod.withdraw(-10.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Withdrawal amount cannot be negative");
        }
        
        @Test
        @DisplayName("Should throw exception when withdrawing non-operational rod")
        void shouldThrowExceptionWhenWithdrawingNonOperationalRod() {
            // Given
            controlRod.setOperational(false);
            
            // Then
            assertThatThrownBy(() -> controlRod.withdraw(10.0))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Control rod is not operational");
        }
    }
    
    @Nested
    @DisplayName("Full Insertion and Withdrawal Tests")
    class FullInsertionAndWithdrawalTests {
        
        @Test
        @DisplayName("Should fully insert control rod")
        void shouldFullyInsertControlRod() {
            // Given
            controlRod.setInsertionLevel(25.0);
            
            // When
            controlRod.fullyInsert();
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(100.0);
        }
        
        @Test
        @DisplayName("Should throw exception when fully inserting non-operational rod")
        void shouldThrowExceptionWhenFullyInsertingNonOperationalRod() {
            // Given
            controlRod.setOperational(false);
            
            // Then
            assertThatThrownBy(() -> controlRod.fullyInsert())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Control rod is not operational");
        }
        
        @Test
        @DisplayName("Should fully withdraw control rod")
        void shouldFullyWithdrawControlRod() {
            // Given
            controlRod.setInsertionLevel(75.0);
            
            // When
            controlRod.fullyWithdraw();
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(0.0);
        }
        
        @Test
        @DisplayName("Should throw exception when fully withdrawing non-operational rod")
        void shouldThrowExceptionWhenFullyWithdrawingNonOperationalRod() {
            // Given
            controlRod.setOperational(false);
            
            // Then
            assertThatThrownBy(() -> controlRod.fullyWithdraw())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Control rod is not operational");
        }
    }
    
    @Nested
    @DisplayName("Status Check Tests")
    class StatusCheckTests {
        
        @Test
        @DisplayName("Should correctly identify fully inserted rod")
        void shouldCorrectlyIdentifyFullyInsertedRod() {
            // Given
            controlRod.setInsertionLevel(100.0);
            
            // Then
            assertThat(controlRod.isFullyInserted()).isTrue();
            
            // Given
            controlRod.setInsertionLevel(99.9);
            
            // Then
            assertThat(controlRod.isFullyInserted()).isFalse();
        }
        
        @Test
        @DisplayName("Should correctly identify fully withdrawn rod")
        void shouldCorrectlyIdentifyFullyWithdrawnRod() {
            // Given
            controlRod.setInsertionLevel(0.0);
            
            // Then
            assertThat(controlRod.isFullyWithdrawn()).isTrue();
            
            // Given
            controlRod.setInsertionLevel(0.1);
            
            // Then
            assertThat(controlRod.isFullyWithdrawn()).isFalse();
        }
        
        @Test
        @DisplayName("Should calculate effectiveness correctly")
        void shouldCalculateEffectivenessCorrectly() {
            // Given - Operational rod at 50% insertion
            assertThat(controlRod.getEffectiveness()).isEqualTo(0.5);
            
            // Given - Operational rod at 100% insertion
            controlRod.setInsertionLevel(100.0);
            assertThat(controlRod.getEffectiveness()).isEqualTo(1.0);
            
            // Given - Operational rod at 0% insertion
            controlRod.setInsertionLevel(0.0);
            assertThat(controlRod.getEffectiveness()).isEqualTo(0.0);
            
            // Given - Non-operational rod
            controlRod.setOperational(false);
            assertThat(controlRod.getEffectiveness()).isEqualTo(0.0);
        }
    }
    
    @Nested
    @DisplayName("Movement Simulation Tests")
    class MovementSimulationTests {
        
        @Test
        @DisplayName("Should simulate insertion movement correctly")
        void shouldSimulateInsertionMovementCorrectly() {
            // Given
            controlRod.setCurrentInsertionSpeed(5.0); // 5 mm/s insertion
            double initialLevel = controlRod.getInsertionLevel();
            
            // When
            controlRod.simulateMovement(2.0); // 2 seconds
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(initialLevel + 10.0);
        }
        
        @Test
        @DisplayName("Should simulate withdrawal movement correctly")
        void shouldSimulateWithdrawalMovementCorrectly() {
            // Given
            controlRod.setCurrentInsertionSpeed(-3.0); // 3 mm/s withdrawal
            double initialLevel = controlRod.getInsertionLevel();
            
            // When
            controlRod.simulateMovement(1.5); // 1.5 seconds
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(initialLevel - 4.5);
        }
        
        @Test
        @DisplayName("Should not move when speed is zero")
        void shouldNotMoveWhenSpeedIsZero() {
            // Given
            controlRod.setCurrentInsertionSpeed(0.0);
            double initialLevel = controlRod.getInsertionLevel();
            
            // When
            controlRod.simulateMovement(5.0);
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(initialLevel);
        }
        
        @Test
        @DisplayName("Should not move when non-operational")
        void shouldNotMoveWhenNonOperational() {
            // Given
            controlRod.setOperational(false);
            controlRod.setCurrentInsertionSpeed(10.0);
            double initialLevel = controlRod.getInsertionLevel();
            
            // When
            controlRod.simulateMovement(2.0);
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(initialLevel);
        }
        
        @Test
        @DisplayName("Should clamp movement to valid range")
        void shouldClampMovementToValidRange() {
            // Given
            controlRod.setInsertionLevel(10.0);
            controlRod.setCurrentInsertionSpeed(-20.0); // Large withdrawal speed
            
            // When
            controlRod.simulateMovement(1.0);
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(0.0);
        }
    }
    
    @Nested
    @DisplayName("Speed Control Tests")
    class SpeedControlTests {
        
        @Test
        @DisplayName("Should set insertion speed within limits")
        void shouldSetInsertionSpeedWithinLimits() {
            // When
            controlRod.setInsertionSpeed(5.0);
            
            // Then
            assertThat(controlRod.getCurrentInsertionSpeed()).isEqualTo(5.0);
            
            // When
            controlRod.setInsertionSpeed(-3.0);
            
            // Then
            assertThat(controlRod.getCurrentInsertionSpeed()).isEqualTo(-3.0);
        }
        
        @Test
        @DisplayName("Should throw exception for speed exceeding maximum")
        void shouldThrowExceptionForSpeedExceedingMaximum() {
            // Then
            assertThatThrownBy(() -> controlRod.setInsertionSpeed(15.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Speed exceeds maximum insertion speed");
            
            assertThatThrownBy(() -> controlRod.setInsertionSpeed(-15.0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Speed exceeds maximum insertion speed");
        }
        
        @Test
        @DisplayName("Should allow maximum speed")
        void shouldAllowMaximumSpeed() {
            // When
            controlRod.setInsertionSpeed(10.0);
            
            // Then
            assertThat(controlRod.getCurrentInsertionSpeed()).isEqualTo(10.0);
            
            // When
            controlRod.setInsertionSpeed(-10.0);
            
            // Then
            assertThat(controlRod.getCurrentInsertionSpeed()).isEqualTo(-10.0);
        }
    }
    
    @Nested
    @DisplayName("Emergency Operations Tests")
    class EmergencyOperationsTests {
        
        @Test
        @DisplayName("Should perform emergency insertion")
        void shouldPerformEmergencyInsertion() {
            // Given
            controlRod.setInsertionLevel(25.0);
            controlRod.setCurrentInsertionSpeed(0.0);
            
            // When
            controlRod.emergencyInsert();
            
            // Then
            assertThat(controlRod.getInsertionLevel()).isEqualTo(100.0);
            assertThat(controlRod.getCurrentInsertionSpeed()).isEqualTo(10.0);
        }
        
        @Test
        @DisplayName("Should throw exception when emergency inserting non-operational rod")
        void shouldThrowExceptionWhenEmergencyInsertingNonOperationalRod() {
            // Given
            controlRod.setOperational(false);
            
            // Then
            assertThatThrownBy(() -> controlRod.emergencyInsert())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Control rod is not operational");
        }
    }
    
    @Nested
    @DisplayName("Object Methods Tests")
    class ObjectMethodsTests {
        
        @Test
        @DisplayName("Should implement equals correctly")
        void shouldImplementEqualsCorrectly() {
            // Given
            ControlRod rod1 = new ControlRod("CR-001", 50.0);
            ControlRod rod2 = new ControlRod("CR-001", 75.0);
            ControlRod rod3 = new ControlRod("CR-002", 50.0);
            
            // Then
            assertThat(rod1).isEqualTo(rod2);
            assertThat(rod1).isNotEqualTo(rod3);
            assertThat(rod1).isNotEqualTo(null);
            assertThat(rod1).isEqualTo(rod1);
        }
        
        @Test
        @DisplayName("Should implement hashCode correctly")
        void shouldImplementHashCodeCorrectly() {
            // Given
            ControlRod rod1 = new ControlRod("CR-001", 50.0);
            ControlRod rod2 = new ControlRod("CR-001", 75.0);
            
            // Then
            assertThat(rod1.hashCode()).isEqualTo(rod2.hashCode());
        }
        
        @Test
        @DisplayName("Should implement toString correctly")
        void shouldImplementToStringCorrectly() {
            // Given
            ControlRod rod = new ControlRod("CR-001", 75.0);
            
            // When
            String result = rod.toString();
            
            // Then
            assertThat(result)
                    .contains("CR-001")
                    .contains("75.0%")
                    .contains("operational=true");
        }
    }
    
    @ParameterizedTest
    @CsvSource({
            "0.0, 0.0, 0.0",
            "25.0, 5.0, 30.0",
            "90.0, 15.0, 100.0",
            "100.0, 10.0, 100.0"
    })
    @DisplayName("Should handle various insertion scenarios")
    void shouldHandleVariousInsertionScenarios(double initialLevel, double insertAmount, double expectedLevel) {
        // Given
        controlRod.setInsertionLevel(initialLevel);
        
        // When
        controlRod.insert(insertAmount);
        
        // Then
        assertThat(controlRod.getInsertionLevel()).isEqualTo(expectedLevel);
    }
} 