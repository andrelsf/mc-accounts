package andrelsf.github.com.mcaccounts.services;

import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import reactor.core.publisher.Mono;

public interface BacenClient {

  Mono<Void> postNotification(TransferResponse transferResponse);

}
