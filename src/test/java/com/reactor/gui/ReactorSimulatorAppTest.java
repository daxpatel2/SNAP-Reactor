package com.reactor.gui;

import com.reactor.model.Reactor;
import com.reactor.service.ReactorMonitorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for the ReactorSimulatorApp main application class.
 */
@DisplayName("Reactor Simulator App Tests")
class ReactorSimulatorAppTest {

    @Test
    @DisplayName("Should create application instance")
    void shouldCreateApplicationInstance() {
        // Given & When
        ReactorSimulatorApp app = new ReactorSimulatorApp(new Reactor("1","3 Mile Island"), new ReactorMonitorService());

        // Then
        assertThat(app).isNotNull();
        assertThat(app).isInstanceOf(ReactorSimulatorApp.class);
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