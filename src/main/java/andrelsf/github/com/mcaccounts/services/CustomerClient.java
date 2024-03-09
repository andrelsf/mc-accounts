package andrelsf.github.com.mcaccounts.services;

import andrelsf.github.com.mcaccounts.api.http.responses.CustomerResponse;
import reactor.core.publisher.Mono;

public interface CustomerClient {

  Mono<CustomerResponse> getCustomerById(final String customerId);

}
