package andrelsf.github.com.mcaccounts.configs;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.*;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfiguration {

  private final static Logger logger = LoggerFactory.getLogger(CircuitBreakerConfiguration.class);

  private final BacenCircuitBreakerConfiguration bacenConfig;

  public CircuitBreakerConfiguration(BacenCircuitBreakerConfiguration bacenConfig) {
    this.bacenConfig = bacenConfig;
  }

  @Bean
  public Customizer<ReactiveResilience4JCircuitBreakerFactory> bacenCircuitBreaker() {
    final CircuitBreakerConfig config = CircuitBreakerConfig.custom()
        .slidingWindowType(COUNT_BASED)
        .slidingWindowSize(bacenConfig.getSlidingWindowSize())
        .minimumNumberOfCalls(bacenConfig.getMinimumNumberOfCalls())
        .failureRateThreshold(bacenConfig.getFailureRateThreshold())
        .permittedNumberOfCallsInHalfOpenState(
            bacenConfig.getPermittedNumberOfCallsInHalfOpenState())
        .waitDurationInOpenState(Duration.ofSeconds(bacenConfig.getWaitDurationInOpenState()))
        .build();
    return factory -> {
      factory.configure(builder -> builder.circuitBreakerConfig(config)
          .timeLimiterConfig(TimeLimiterConfig.custom()
              .timeoutDuration(Duration.ofSeconds(2))
              .build()), bacenConfig.getBackendName());
    };
  }

  @Bean
  public ReactiveCircuitBreaker apiBacenCircuitBreaker(ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory) {
    return reactiveCircuitBreakerFactory.create(bacenConfig.getBackendName());
  }

  @Bean
  public RegistryEventConsumer<CircuitBreaker> circuitBreakerLogger() {
    return new RegistryEventConsumer<CircuitBreaker>() {
      @Override
      public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
        entryAddedEvent.getAddedEntry()
            .getEventPublisher()
            .onStateTransition(event -> logger.info(event.toString()));
      }

      @Override
      public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
        entryRemoveEvent.getRemovedEntry()
            .getEventPublisher()
            .onStateTransition(event -> logger.info(event.toString()));
      }

      @Override
      public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
        entryReplacedEvent.getNewEntry()
            .getEventPublisher()
            .onStateTransition(event -> logger.info(event.toString()));
      }
    };
  }
}
