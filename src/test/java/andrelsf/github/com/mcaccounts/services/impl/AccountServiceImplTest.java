package andrelsf.github.com.mcaccounts.services.impl;

import static andrelsf.github.com.mcaccounts.entities.domains.AccountStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import andrelsf.github.com.mcaccounts.api.http.requests.PatchTransferLimitRequest;
import andrelsf.github.com.mcaccounts.api.http.requests.PostTransferRequest;
import andrelsf.github.com.mcaccounts.api.http.requests.ToAccountRequest;
import andrelsf.github.com.mcaccounts.api.http.responses.BalanceResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.CustomerResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.FromAccountResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.ToAccountResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import andrelsf.github.com.mcaccounts.entities.domains.AccountEntity;
import andrelsf.github.com.mcaccounts.handlers.exceptions.AccountNotFoundException;
import andrelsf.github.com.mcaccounts.repositories.AccountRepository;
import andrelsf.github.com.mcaccounts.services.BacenClient;
import andrelsf.github.com.mcaccounts.services.CustomerClient;
import andrelsf.github.com.mcaccounts.services.QueueService;
import andrelsf.github.com.mcaccounts.utils.ApiUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

  @Mock
  private BacenClient bacenClient;
  @Mock
  private QueueService queueService;
  @Mock
  private AccountRepository repository;
  @Mock
  private CustomerClient customerClient;

  @InjectMocks
  private AccountServiceImpl accountService;

  @Test
  @DisplayName("Dado um custormeId valido ao consultar returne o Cliente e consulte na API Cadastro"
      + "(Customers) e retorne o Saldo em conta.")
  void test_checkAccountBalance_success() {
    final String customerId = UUID.randomUUID().toString();
    final AccountEntity account = buildAccountEntity(customerId);
    final CustomerResponse customerResponse = buildCustomerResponse(customerId);

    when(repository.findAccountEntityByCustomerIdAndStatusIs(customerId, ACTIVE.name()))
        .thenReturn(Mono.just(account));
    when(customerClient.getCustomerById(customerId))
        .thenReturn(Mono.just(customerResponse));

    Optional<BalanceResponse> balanceResponseOptional = accountService.checkAccountBalance(customerId)
        .blockOptional();

    assertThat(balanceResponseOptional)
        .isNotEmpty()
        .isInstanceOf(Optional.class);

    final BalanceResponse balanceResponse = balanceResponseOptional.get();
    assertThat(balanceResponse)
        .isNotNull()
        .isInstanceOf(BalanceResponse.class);

    assertThat(balanceResponse.fullName())
        .isNotEmpty()
        .isEqualTo(customerResponse.fullName());

    assertThat(balanceResponse.balance())
        .isNotNull()
        .isInstanceOf(BigDecimal.class);
  }

  @Test
  @DisplayName("Dado o customerId invalid deve ao consultar lance uma exception account nao encontrada")
  void test_checkAccountBalance_fail() {
    final String customerId = UUID.randomUUID().toString();

    when(repository.findAccountEntityByCustomerIdAndStatusIs(customerId, ACTIVE.name()))
        .thenReturn(Mono.empty());

    when(customerClient.getCustomerById(customerId))
        .thenReturn(Mono.empty());

    assertThatThrownBy(() -> accountService.checkAccountBalance(customerId).block())
        .isInstanceOf(AccountNotFoundException.class)
        .hasMessage("Account not found by customerId=".concat(customerId));
  }

  @Test
  @DisplayName("Deve realizar a atualizacao do limite de transferencia para um cliente valido")
  void test_updateTransferLimit_success() {
    final BigDecimal amount = BigDecimal.valueOf(1000.0F);
    final PatchTransferLimitRequest transferLimitRequest = new PatchTransferLimitRequest(amount);
    final String customerId = UUID.randomUUID().toString();
    AccountEntity account = buildAccountEntity(customerId);

    when(repository.findAccountEntityByCustomerIdAndStatusIs(customerId, ACTIVE.name()))
        .thenReturn(Mono.just(account));

    account.setDailyTransferLimit(amount);

    when(repository.save(account))
        .thenReturn(Mono.create(MonoSink::success));

    assertDoesNotThrow(() ->
        accountService.updateTransferLimit(customerId, transferLimitRequest).block());
  }

  @Test
  @DisplayName("Dado um customerId e dados para transferencia validos deve realizar a transferrencia entre contas.")
  void test_doTransfer_success() {
    final String customerId = UUID.randomUUID().toString();
    final PostTransferRequest request = buildTransferRequest();
    final AccountEntity fromAccount = buildAccountEntity(customerId);
    final AccountEntity toAccount = buildToAccountEntity(request);
    final TransferResponse transferResponseMock = buildTransferResponse(
        fromAccount, toAccount, request.amount());

    when(repository.findAccountEntityByCustomerIdAndBalanceGreaterThanEqualAndDailyTransferLimitGreaterThanEqualAndStatusIs(
        customerId, request.amount(), request.amount(), ACTIVE.name()))
        .thenReturn(Mono.just(fromAccount));

    when(repository.findAccountEntityByAgencyAndAccountNumberAndStatusIs(
        request.toAccount().agency(), request.toAccount().accountNumber(), ACTIVE.name()))
        .thenReturn(Mono.just(toAccount));

    when(repository.saveAll(List.of(fromAccount, toAccount)))
        .thenReturn(Flux.just(fromAccount, toAccount));

    when(bacenClient.postNotification(transferResponseMock))
        .thenReturn(Mono.empty());

    final TransferResponse transferResponse = accountService.doTransfer(customerId, request)
        .block();

    assertThat(transferResponse)
        .isNotNull()
        .isInstanceOf(TransferResponse.class);

    assertThat(transferResponse.amount())
        .isNotNull()
        .isInstanceOf(BigDecimal.class)
        .isEqualTo(request.amount());
  }

  private TransferResponse buildTransferResponse(
      final AccountEntity fromAccount, final AccountEntity toAccount, BigDecimal amount) {
    final LocalDateTime transferDate = ApiUtils.getLocalDateTimeNow();
    return new TransferResponse(
        new FromAccountResponse(fromAccount.getAgency(), fromAccount.getAccountNumber()),
        new ToAccountResponse(toAccount.getAgency(), toAccount.getAccountNumber()),
        amount,
        transferDate.toString()
    );
  }

  private AccountEntity buildToAccountEntity(final PostTransferRequest request) {
    final LocalDateTime localDateTime = ApiUtils.getLocalDateTimeNow();
    return new AccountEntity(
        UUID.randomUUID().toString(),
        UUID.randomUUID().toString(),
        request.toAccount().agency(),
        request.toAccount().accountNumber(),
        ACTIVE.name(),
        BigDecimal.valueOf(1000.00f),
        BigDecimal.valueOf(1000.00f),
        localDateTime,
        localDateTime
    );
  }

  private PostTransferRequest buildTransferRequest() {
    return new PostTransferRequest(
        new ToAccountRequest(
            "Alice",
            321,
            7654321),
        BigDecimal.valueOf(100.00f)
    );
  }

  private CustomerResponse buildCustomerResponse(String customerId) {
    return new CustomerResponse(customerId, "Foo", "11122233344");
  }

  private AccountEntity buildAccountEntity(final String customerId) {
    return new AccountEntity(
        UUID.randomUUID().toString(),
        customerId,
        123,
        1234567,
        ACTIVE.name(),
        BigDecimal.valueOf(1000.00f),
        BigDecimal.valueOf(1000.00f),
        LocalDateTime.now(),
        LocalDateTime.now()
    );
  }
}
