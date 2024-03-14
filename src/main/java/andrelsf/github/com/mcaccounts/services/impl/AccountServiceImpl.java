package andrelsf.github.com.mcaccounts.services.impl;

import static andrelsf.github.com.mcaccounts.entities.domains.AccountStatus.ACTIVE;
import static andrelsf.github.com.mcaccounts.entities.events.QueueType.DLQ;
import static andrelsf.github.com.mcaccounts.entities.events.StatusMessage.PENDING;
import static reactor.core.publisher.Mono.error;

import andrelsf.github.com.mcaccounts.api.http.requests.PostTransferRequest;
import andrelsf.github.com.mcaccounts.api.http.responses.BalanceResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import andrelsf.github.com.mcaccounts.entities.domains.AccountEntity;
import andrelsf.github.com.mcaccounts.entities.events.InputMessage;
import andrelsf.github.com.mcaccounts.handlers.exceptions.ToAccountNotFoundException;
import andrelsf.github.com.mcaccounts.handlers.exceptions.UnableToTransfer;
import andrelsf.github.com.mcaccounts.repositories.AccountRepository;
import andrelsf.github.com.mcaccounts.services.AccountService;
import andrelsf.github.com.mcaccounts.services.BacenClient;
import andrelsf.github.com.mcaccounts.services.CustomerClient;
import andrelsf.github.com.mcaccounts.services.QueueService;
import andrelsf.github.com.mcaccounts.utils.Mapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class AccountServiceImpl implements AccountService {

  private final BacenClient bacenClient;
  private final QueueService queueService;
  private final AccountRepository repository;
  private final CustomerClient customerClient;

  public AccountServiceImpl(
      AccountRepository repository,
      BacenClient bacenClient,
      QueueService queueService,
      CustomerClient customerClient) {
    this.repository = repository;
    this.bacenClient = bacenClient;
    this.queueService = queueService;
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
        .switchIfEmpty(error(
            new UnableToTransfer("Customer unable to make transfer.\n"
                + "Check account balance and daily transfer limit.")))
        .zipWith(
            repository.findAccountEntityByAgencyAndAccountNumberAndStatusIs(
                request.toAccount().agency(), request.toAccount().accountNumber(), ACTIVE.name())
                .switchIfEmpty(error(new ToAccountNotFoundException("To Account not found by Agency and Account Number.")))
        )
        .publishOn(Schedulers.boundedElastic())
        .flatMap(tuple -> {
          final String transactionId = UUID.randomUUID().toString();
          final LocalDateTime transferDate = LocalDateTime.now();

          AccountEntity fromAccount = tuple.getT1();
          fromAccount.debit(request.amount(), transferDate);
          fromAccount.decreaseDailyTransferLimit(request.amount());

          AccountEntity toAccount = tuple.getT2();
          toAccount.credit(request.amount(), transferDate);

          repository.saveAll(List.of(fromAccount, toAccount))
              .publishOn(Schedulers.boundedElastic())
              .subscribe();

          final TransferResponse transferResponse = Mapper.entitiesToTransferResponse(
              transactionId, fromAccount, toAccount, request.amount(), transferDate.toString());
          bacenClient.postNotification(transferResponse)
              .switchIfEmpty(queueService.send(PENDING, InputMessage.of(transferResponse), DLQ))
              .subscribe();
          return Mono.just(transferResponse);
        });
  }
}
