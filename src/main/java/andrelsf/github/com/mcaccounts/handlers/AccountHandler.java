package andrelsf.github.com.mcaccounts.handlers;

import andrelsf.github.com.mcaccounts.api.http.requests.PostTransferRequest;
import andrelsf.github.com.mcaccounts.services.CustomerService;
import andrelsf.github.com.mcaccounts.services.impl.RequestValidator;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class AccountHandler {

  private final CustomerService customerService;
  private final RequestValidator requestValidator;

  public AccountHandler(CustomerService customerService, RequestValidator requestValidator) {
    this.customerService = customerService;
    this.requestValidator = requestValidator;
  }

  public Mono<ServerResponse> getBalance(final ServerRequest request) {
    final String customerId = request.pathVariable("customerId");
    return customerService.checkAccountBalance(customerId)
        .flatMap(balanceResponse ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(balanceResponse)))
        .switchIfEmpty(ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> postTransfers(final ServerRequest request) {
    final String customerId = request.pathVariable("customerId");
    return request.bodyToMono(PostTransferRequest.class)
        .doOnNext(requestValidator::validate)
        .cast(PostTransferRequest.class)
        .flatMap(postTransferRequest ->
            customerService.doTransfer(customerId, postTransferRequest))
        .flatMap(transferResponse ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(transferResponse)))
        .switchIfEmpty(ServerResponse.unprocessableEntity().build());
  }
}
