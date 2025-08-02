# Reactor Simulator - Testing Learning Project

A comprehensive Java Maven project designed to help you learn and practice testing with **JUnit 5** and **AssertJ**. This project simulates a nuclear reactor system with various components and operations that provide rich testing scenarios.

## Current Tests
![Tests](https://github.com/daxpatel2/SNAP-Reactor/actions/workflows/test.yml/badge.svg)


## 🎯 Learning Objectives

This project will help you master:

- **JUnit 5** testing framework
- **AssertJ** fluent assertions
- **Parameterized tests**
- **Nested test classes**
- **Test organization and naming**
- **Exception testing**
- **Complex object testing**
- **Service layer testing**
- **Business logic validation**
- **Mockito mocking framework**
- **GUI testing techniques**
- **Java Swing application testing**
- **Mocking dependencies and services**

## 🏗️ Project Structure

```
reactor-simulator/
├── src/
│   ├── main/java/com/reactor/
│   │   ├── model/
│   │   │   ├── Reactor.java          # Main reactor simulation class
│   │   │   └── ControlRod.java       # Control rod component
│   │   ├── service/
│   │   │   ├── ReactorMonitorService.java    # Monitoring and analysis service
│   │   │   ├── ReactorHealthReport.java      # Health analysis results
│   │   │   └── PerformanceReport.java        # Performance metrics
│   │   └── gui/
│   │       └── ReactorSimulatorApp.java      # Main Java Swing application
│   └── test/java/com/reactor/
│       ├── model/
│       │   ├── ReactorTest.java      # Comprehensive reactor tests
│       │   └── ControlRodTest.java   # Control rod tests
│       ├── service/
│       │   └── ReactorMonitorServiceTest.java # Service layer tests
│       └── gui/
│           ├── ReactorSimulatorAppTest.java      # Application tests
│           └── ReactorSimulatorControllerTest.java # Swing app tests with mocking
├── pom.xml                           # Maven configuration
└── README.md                         # This file
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Project

1. **Clone or navigate to the project directory**
   ```bash
   cd reactor-simulator
   ```

2. **Compile the project**
   ```bash
   mvn compile
   ```

3. **Run all tests**
   ```bash
   mvn test
   ```

4. **Run tests with detailed output**
   ```bash
   mvn test -Dtest=ReactorTest
   ```

5. **Run the GUI application**
   ```bash
   mvn exec:java -Dexec.mainClass="com.reactor.gui.ReactorSimulatorApp"
   ```

6. **Run GUI tests specifically**
   ```bash
   mvn test -Dtest=ReactorSimulatorControllerTest
   ```

## 📚 Learning Path

### 1. Start with Basic Tests

- **Constructor testing** - Verifying object creation
- **Getter/Setter testing** - Testing basic property access
- **Exception testing** - Using `assertThatThrownBy()`
- **Parameterized tests** - Testing multiple scenarios efficiently

### 2. Explore Advanced Testing Patterns

The test classes showcase:

- **Nested test classes** - Organizing related tests
- **@BeforeEach setup** - Test initialization
- **@DisplayName** - Readable test descriptions
- **Complex assertions** - Testing collections, objects, and conditions

### 3. Study Service Layer Testing

`ReactorMonitorServiceTest.java` demonstrates:

- **Service method testing** - Testing business logic
- **Report generation testing** - Testing complex object creation
- **Conditional logic testing** - Testing different scenarios
- **Integration testing** - Testing interactions between components

### 4. Explore GUI Testing and Mocking

`ReactorSimulatorControllerTest.java` demonstrates:

- **Mockito framework usage** - Mocking dependencies and services
- **Swing application testing** - Testing Java Swing GUI logic
- **Mocking examples** - Spy usage, argument matchers, exception testing
- **Service layer mocking** - Mocking complex service interactions
- **Integration testing** - Testing real components with mocked dependencies

## 🧪 Testing Techniques Demonstrated

### AssertJ Fluent Assertions

```java
// Basic assertions
assertThat(reactor.getTemperature()).isEqualTo(25.0);
assertThat(reactor.getStatus()).isEqualTo(ReactorStatus.SHUTDOWN);

// Exception testing
assertThatThrownBy(() -> reactor.setTemperature(-10.0))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessage("Temperature cannot be negative");

// Collection testing
assertThat(controlRods).hasSize(10);
assertThat(controlRods)
    .extracting(ControlRod::getId)
    .containsExactlyInAnyOrder("CR-1", "CR-2", "CR-3");

// Complex object testing
assertThat(reactor).satisfies(r -> {
    assertThat(r.getTemperature()).isEqualTo(25.0);
    assertThat(r.getPressure()).isEqualTo(0.1);
});
```

### Mockito Mocking Examples

```java
// Basic mocking
@Mock private Reactor mockReactor;
@Mock private ReactorMonitorService mockMonitorService;

when(mockReactor.getStatus()).thenReturn(ReactorStatus.OPERATIONAL);
when(mockMonitorService.analyzeHealth(mockReactor)).thenReturn(mockHealthReport);

// Spy usage
Reactor spyReactor = spy(realReactor);
doReturn(ReactorStatus.OPERATIONAL).when(spyReactor).getStatus();

// Argument matchers
doNothing().when(mockReactor).adjustPower(anyDouble());
verify(mockReactor).adjustPower(800.0);

// Exception testing with mocks
doThrow(new IllegalStateException("Test exception")).when(mockReactor).startUp();
assertThatThrownBy(() -> mockReactor.startUp())
    .isInstanceOf(IllegalStateException.class);

// Service layer mocking
when(mockMonitorService.generatePerformanceReport(mockReactor))
    .thenReturn(mockPerformanceReport);
when(mockPerformanceReport.getCurrentPower()).thenReturn(900.0);
```

### JUnit 5 Features

```java
@Nested
@DisplayName("Startup Sequence Tests")
class StartupSequenceTests {
    
    @Test
    @DisplayName("Should start up successfully from shutdown status")
    void shouldStartUpSuccessfullyFromShutdownStatus() {
        // Test implementation
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {0.0, 50.0, 100.0})
    @DisplayName("Should accept valid fuel levels")
    void shouldAcceptValidFuelLevels(double fuelLevel) {
        // Parameterized test
    }
}
```

### Parameterized Tests

```java
@ParameterizedTest
@CsvSource({
    "0.0, 300.0, 15.0",
    "500.0, 400.0, 17.5",
    "1000.0, 500.0, 20.0",
    "1200.0, 540.0, 21.0"
})
@DisplayName("Should adjust power and update temperature and pressure")
void shouldAdjustPowerAndUpdateTemperatureAndPressure(
    double targetPower, double expectedTemp, double expectedPressure) {
    // Test implementation
}
```

## 🔧 Key Testing Concepts

### 1. Given-When-Then Pattern

All tests follow the **Given-When-Then** pattern:

```java
@Test
void shouldStartUpSuccessfullyFromShutdownStatus() {
    // Given - Setup test conditions
    assertThat(reactor.getStatus()).isEqualTo(ReactorStatus.SHUTDOWN);
    assertThat(reactor.getFuelLevel()).isEqualTo(100.0);
    
    // When - Execute the action being tested
    reactor.startUp();
    
    // Then - Verify the results
    assertThat(reactor.getStatus()).isEqualTo(ReactorStatus.STARTING_UP);
    assertThat(reactor.getTemperature()).isEqualTo(100.0);
    assertThat(reactor.getPressure()).isEqualTo(1.0);
}
```

### 2. Exception Testing

```java
@Test
void shouldThrowExceptionForNegativeTemperature() {
    // Then
    assertThatThrownBy(() -> reactor.setTemperature(-10.0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Temperature cannot be negative");
}
```

### 3. Collection Testing

```java
@Test
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
}
```

### Extend Testing

1. **Performance Testing** - Add JMH benchmarks
2. **Property-Based Testing** - Use jqwik for property-based tests
3. **Mutation Testing** - Use PIT for mutation testing
4. **Contract Testing** - Add Pact for service contracts

## 📖 Docs

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Testing Best Practices](https://martinfowler.com/articles/practical-test-pyramid.html)
- [Java Testing Patterns](https://github.com/testcontainers/testcontainers-java)