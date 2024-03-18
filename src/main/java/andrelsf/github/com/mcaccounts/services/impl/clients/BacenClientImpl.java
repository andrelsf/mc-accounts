package andrelsf.github.com.mcaccounts.services.impl.clients;

import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import andrelsf.github.com.mcaccounts.handlers.exceptions.IntegrationException;
import andrelsf.github.com.mcaccounts.services.BacenClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class BacenClientImpl implements BacenClient {

  private final Logger logger = LoggerFactory.getLogger(BacenClient.class);
  private static final String URI_BACEN;
  private static final String BACEN_ERROR_MESSAGE;

  static {
    URI_BACEN = "/api/v1/bacen/notifications";
    BACEN_ERROR_MESSAGE = "Error while performing the notification Bacen.";
  }

  private final WebClient apiBacen;
  private final ReactiveCircuitBreaker apiBacenCircuitBreaker;

  public BacenClientImpl(WebClient apiBacen, ReactiveCircuitBreaker apiBacenCircuitBreaker) {
    this.apiBacen = apiBacen;
    this.apiBacenCircuitBreaker = apiBacenCircuitBreaker;
  }

  @Override
  public Mono<Void> postNotification(final TransferResponse transferResponse) {
    return apiBacen.post()
        .uri(URI_BACEN)
        .bodyValue(transferResponse)
        .retrieve()
        .onStatus(HttpStatusCode::isError, this::postNotificationError)
        .bodyToMono(Void.class)
        .publishOn(Schedulers.boundedElastic())
        .transform(object -> apiBacenCircuitBreaker.run(object, this::postNotificationFallback));
  }

  private Mono<Void> postNotificationFallback(Throwable te) {
    logger.warn("Method fallback called. {}", te.getMessage());
    return Mono.error(new IntegrationException(BACEN_ERROR_MESSAGE));
  }

  private Mono<? extends Throwable> postNotificationError(ClientResponse clientResponse) {
    return clientResponse.createException()
        .flatMap(ex -> {
          logger.error(BACEN_ERROR_MESSAGE.concat(" {}"), ex.getMessage());
          return Mono.error(new IntegrationException(BACEN_ERROR_MESSAGE));
        });
  }
}
