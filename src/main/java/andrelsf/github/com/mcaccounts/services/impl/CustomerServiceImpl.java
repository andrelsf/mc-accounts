package andrelsf.github.com.mcaccounts.services.impl;

import static andrelsf.github.com.mcaccounts.entities.AccountStatus.ACTIVE;

import andrelsf.github.com.mcaccounts.api.http.requests.PostTransferRequest;
import andrelsf.github.com.mcaccounts.api.http.responses.BalanceResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import andrelsf.github.com.mcaccounts.entities.AccountEntity;
import andrelsf.github.com.mcaccounts.repositories.AccountRepository;
import andrelsf.github.com.mcaccounts.services.CustomerService;
import andrelsf.github.com.mcaccounts.services.impl.validator.CustomerClient;
import andrelsf.github.com.mcaccounts.utils.Mapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class CustomerServiceImpl implements CustomerService {

  private final static Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

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
    return repository.findAccountEntityByCustomerIdAndStatusIs(customerId, ACTIVE.name())
        .zipWith(customerClient.getCustomerById(customerId))
        .map(tuple -> Mapper.entityToBalanceResponse(tuple.getT1(), tuple.getT2()));
  }

  @Override
  @Transactional
  public Mono<TransferResponse> doTransfer(final String customerId, final PostTransferRequest request) {
    return repository.findAccountEntityByCustomerIdAndBalanceGreaterThanEqualAndDailyTransferLimitGreaterThanEqualAndStatusIs(
          customerId, request.amount(), request.amount(), ACTIVE.name()
        )
        .zipWith(
            repository.findAccountEntityByAgencyAndAccountNumberAndStatusIs(
                request.toAccount().agency(), request.toAccount().accountNumber(), ACTIVE.name())
        )
        .publishOn(Schedulers.boundedElastic())
        .flatMap(tuple -> {
          final String transactionId = UUID.randomUUID().toString();
          final LocalDateTime transferDate = LocalDateTime.now();

          AccountEntity fromAccount = tuple.getT1();
          fromAccount.debit(request.amount());
          fromAccount.decreaseDailyTransferLimit(request.amount());
          fromAccount.setLastUpdated(transferDate);

          AccountEntity toAccount = tuple.getT2();
          toAccount.credit(request.amount());
          toAccount.setLastUpdated(transferDate);

          repository.saveAll(List.of(fromAccount, toAccount))
              .subscribe((error) -> logger.error("saveAll Error" + error));

          final TransferResponse transferResponse = Mapper.entitiesToTransferResponse(
              transactionId, fromAccount, toAccount, request.amount(), transferDate.toString());
          return Mono.just(transferResponse);
        });
  }
}
