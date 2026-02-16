package io.github.akashgill3.datastar.autoconfigure;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.akashgill3.datastar.Datastar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ExtendWith(MockitoExtension.class)
class DatastarAutoConfigurationTest {

  private ApplicationContextRunner contextRunner;

  @BeforeEach
  void setUp() {
    contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(DatastarAutoConfiguration.class);
  }

  @Test
  void autoConfiguration_shouldCreateDatastarBean() {
    contextRunner
        .withPropertyValues("datastar.enable-logging=true")
        .run(context -> {
          assertNotNull(context.getBean(Datastar.class));
          DatastarProperties properties = context.getBean(DatastarProperties.class);
          assertTrue(properties.enableLogging());
        });
  }

  @Test
  void autoConfiguration_shouldUseDefaultProperties() {
    contextRunner
        .run(context -> {
          assertNotNull(context.getBean(Datastar.class));
          DatastarProperties properties = context.getBean(DatastarProperties.class);
          assertFalse(properties.enableLogging()); // default value
        });
  }

  @Test
  void autoConfiguration_shouldNotCreateBeanWhenCustomBeanExists() {
    contextRunner
        .withUserConfiguration(CustomDatastarConfiguration.class)
        .run(context -> {
          Datastar datastar = context.getBean(Datastar.class);
          assertNotNull(datastar);
          // Verify it's our custom implementation by checking it's not the default
          assertInstanceOf(CustomDatastar.class, datastar);
        });
  }

  @Test
  void datastarProperties_shouldHaveValidDefaults() {
    contextRunner
        .run(context -> {
          DatastarProperties properties = context.getBean(DatastarProperties.class);
          assertNotNull(properties);
          assertFalse(properties.enableLogging());
        });
  }

  @Test
  void datastarProperties_shouldAllowCustomValues() {
    contextRunner
        .withPropertyValues("datastar.enable-logging=false")
        .run(context -> {
          DatastarProperties properties = context.getBean(DatastarProperties.class);
          assertFalse(properties.enableLogging());
        });
  }

  @Configuration
  static class CustomDatastarConfiguration {
    @Bean
    @Primary
    public Datastar customDatastar() {
      return new CustomDatastar();
    }
  }

  static class CustomDatastar extends Datastar {
    CustomDatastar() {
      super(new DatastarProperties(false));
    }
  }
}
