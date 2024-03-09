package andrelsf.github.com.mcaccounts.handlers;

import andrelsf.github.com.mcaccounts.services.CustomerService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class AccountHandler {

  private final CustomerService customerService;

  public AccountHandler(CustomerService customerService) {
    this.customerService = customerService;
  }

  public Mono<ServerResponse> getBalance(final ServerRequest request) {
    final String customerId = request.pathVariable("customerId");
    return customerService.checkAccountBalance(customerId)
        .flatMap(balanceResponse ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(balanceResponse)))
        .switchIfEmpty(ServerResponse.notFound().build())
        .log();
  }
}
