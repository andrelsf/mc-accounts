package andrelsf.github.com.mcaccounts.services.impl.clients;

import andrelsf.github.com.mcaccounts.api.http.responses.CustomerResponse;
import andrelsf.github.com.mcaccounts.services.CustomerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class CustomerClientImpl implements CustomerClient {

  private final Logger logger = LoggerFactory.getLogger(CustomerClientImpl.class);
  private static final String URI_CUSTOMERS;

  static {
    URI_CUSTOMERS = "/customers";
  }

  private final WebClient apiCustomers;

  public CustomerClientImpl(WebClient apiCustomers) {
    this.apiCustomers = apiCustomers;
  }

  public Mono<CustomerResponse> getCustomerById(final String customerId) {
    return apiCustomers.get()
        .uri(URI_CUSTOMERS.concat("/{customerId}"), customerId)
        .retrieve()
        .bodyToMono(CustomerResponse.class)
        .publishOn(Schedulers.boundedElastic())
        .doOnError(ex -> logger.error("Fail to call API Customers. ERROR: {}", ex.getMessage()))
        .doOnSuccess(response -> logger.info("Call API Customers by customerId={} success", customerId));
  }
}
