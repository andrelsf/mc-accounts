package andrelsf.github.com.mcaccounts.services.impl;

import andrelsf.github.com.mcaccounts.api.http.responses.BalanceResponse;
import andrelsf.github.com.mcaccounts.entities.AccountStatus;
import andrelsf.github.com.mcaccounts.repositories.AccountRepository;
import andrelsf.github.com.mcaccounts.services.CustomerClient;
import andrelsf.github.com.mcaccounts.services.CustomerService;
import andrelsf.github.com.mcaccounts.utils.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

  private final AccountRepository repository;
  private final CustomerClient customerClient;

  public CustomerServiceImpl(
      AccountRepository repository,
      CustomerClient customerClient) {
    this.repository = repository;
    this.customerClient = customerClient;
  }

  @Override
  @Transactional(readOnly = true)
  public Mono<BalanceResponse> checkAccountBalance(final String customerId) {
    return repository.findAccountEntityByCustomerIdAndStatusIs(customerId, AccountStatus.ACTIVE.name())
        .zipWith(customerClient.getCustomerById(customerId))
        .map(tuple -> Mapper.entityToBalanceResponse(tuple.getT1(), tuple.getT2()));
  }
}
