package andrelsf.github.com.mcaccounts.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "circuit-breakers.api-bacen")
public class BacenCircuitBreakerConfiguration {

  private int slidingWindowSize;
  private int minimumNumberOfCalls;

  private float failureRateThreshold;
  private int permittedNumberOfCallsInHalfOpenState;
  private long waitDurationInOpenState;


  public BacenCircuitBreakerConfiguration() {
  }

  public BacenCircuitBreakerConfiguration(
      int slidingWindowSize,
      int minimumNumberOfCalls,
      float failureRateThreshold,
      int permittedNumberOfCallsInHalfOpenState,
      long waitDurationInOpenState) {
    this.slidingWindowSize = slidingWindowSize;
    this.minimumNumberOfCalls = minimumNumberOfCalls;
    this.failureRateThreshold = failureRateThreshold;
    this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
    this.waitDurationInOpenState = waitDurationInOpenState;
  }

  public int getSlidingWindowSize() {
    return slidingWindowSize;
  }

  public int getMinimumNumberOfCalls() {
    return minimumNumberOfCalls;
  }

  public float getFailureRateThreshold() {
    return failureRateThreshold;
  }

  public int getPermittedNumberOfCallsInHalfOpenState() {
    return permittedNumberOfCallsInHalfOpenState;
  }

  public long getWaitDurationInOpenState() {
    return waitDurationInOpenState;
  }

  public void setFailureRateThreshold(String failureRateThreshold) {
    this.failureRateThreshold = Float.parseFloat(failureRateThreshold);
  }

  public void setSlidingWindowSize(int slidingWindowSize) {
    this.slidingWindowSize = slidingWindowSize;
  }

  public void setMinimumNumberOfCalls(String minimumNumberOfCalls) {
    this.minimumNumberOfCalls = Integer.parseInt(minimumNumberOfCalls);
  }

  public void setPermittedNumberOfCallsInHalfOpenState(String permittedNumberOfCallsInHalfOpenState) {
    this.permittedNumberOfCallsInHalfOpenState = Integer.parseInt(permittedNumberOfCallsInHalfOpenState);
  }

  public void setWaitDurationInOpenState(String waitDurationInOpenState) {
    this.waitDurationInOpenState = Long.parseLong(waitDurationInOpenState);
  }
}
