package andrelsf.github.com.mcaccounts.services.impl;

import andrelsf.github.com.mcaccounts.api.http.responses.CustomerResponse;
import andrelsf.github.com.mcaccounts.services.CustomerService;
import andrelsf.github.com.mcaccounts.services.clients.CustomerClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

  private final CustomerClient customerClient;

  public CustomerServiceImpl(CustomerClient customerClient) {
    this.customerClient = customerClient;
  }

  @Override
  public Mono<CustomerResponse> checkAccountBalance(final String customerId) {
    // TODO: verificar sem conta esta ativa
    return customerClient.getCustomerById(customerId);
  }
}
