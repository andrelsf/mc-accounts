package andrelsf.github.com.mcaccounts.api.http.resources;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import andrelsf.github.com.mcaccounts.handlers.AccountHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class AccountResource {

  @Bean
  RouterFunction<ServerResponse> routes(final AccountHandler accountHandler) {
    return RouterFunctions.route()
        .path("/accounts", builder -> builder
            .GET("/{customerId}/balance", accept(APPLICATION_JSON), accountHandler::getBalance)
            .POST("/{customerId}/transfers", accept(APPLICATION_JSON), accountHandler::postTransfers)
        ).build();
  }
}
