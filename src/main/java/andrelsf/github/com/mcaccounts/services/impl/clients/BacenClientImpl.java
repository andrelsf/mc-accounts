package andrelsf.github.com.mcaccounts.services.impl.clients;

import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import andrelsf.github.com.mcaccounts.handlers.exceptions.IntegrationException;
import andrelsf.github.com.mcaccounts.services.BacenClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  static {
    URI_BACEN = "/api/v1/bacen/notifications";
  }

  private final WebClient apiBacen;

  public BacenClientImpl(WebClient apiBacen) {
    this.apiBacen = apiBacen;
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
        .doOnError(throwable ->
            logger.error("Error while performing the notification Bacen. {}", throwable.getMessage()));
  }

  private Mono<? extends Throwable> postNotificationError(ClientResponse clientResponse) {
    return clientResponse.createException()
        .flatMap(ex -> Mono.error(new IntegrationException(
            "Error while performing the notification Bacen.")));
  }
}
