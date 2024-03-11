package andrelsf.github.com.mcaccounts.handlers;

import andrelsf.github.com.mcaccounts.api.http.requests.PostTransferRequest;
import andrelsf.github.com.mcaccounts.services.AccountService;
import andrelsf.github.com.mcaccounts.services.impl.validator.RequestValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class AccountHandler {

  private final AccountService accountService;
  private final RequestValidator requestValidator;

  public AccountHandler(AccountService accountService, RequestValidator requestValidator) {
    this.accountService = accountService;
    this.requestValidator = requestValidator;
  }

  public Mono<ServerResponse> getBalance(final ServerRequest request) {
    final String customerId = request.pathVariable("customerId");
    return accountService.checkAccountBalance(customerId)
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
            accountService.doTransfer(customerId, postTransferRequest))
        .flatMap(transferResponse ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(transferResponse)))
        .switchIfEmpty(ServerResponse.unprocessableEntity().build());
  }
}
