package andrelsf.github.com.mcaccounts.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import andrelsf.github.com.mcaccounts.api.http.requests.PatchTransferLimitRequest;
import andrelsf.github.com.mcaccounts.api.http.requests.PostTransferRequest;
import andrelsf.github.com.mcaccounts.api.http.requests.ToAccountRequest;
import andrelsf.github.com.mcaccounts.api.http.resources.AccountResource;
import andrelsf.github.com.mcaccounts.api.http.responses.BalanceResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.FromAccountResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.ToAccountResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import andrelsf.github.com.mcaccounts.services.AccountService;
import andrelsf.github.com.mcaccounts.services.impl.validator.RequestValidator;
import andrelsf.github.com.mcaccounts.utils.ApiUtils;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
    AccountResource.class,
    AccountHandler.class
})
public class AccountHandlerTest {

  @Autowired
  private ApplicationContext context;

  @MockBean
  private AccountService accountService;

  @MockBean
  private RequestValidator requestValidator;

  private WebTestClient webTestClient;

  @Before
  public void setUp() {
    webTestClient = WebTestClient.bindToApplicationContext(context).build();
  }

  @Test
  @DisplayName("Dada a request GET Balance para um cliente valido retorne sucesso.")
  public void test_getBalance_success() {
    final String customerId = UUID.randomUUID().toString();
    final BalanceResponse balance = buildBalanceResponse();
    final URI uri = URI.create("/accounts/".concat(customerId).concat("/balance"));

    when(accountService.checkAccountBalance(customerId))
        .thenReturn(Mono.just(balance));

    webTestClient.get()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(BalanceResponse.class)
        .value(response -> {
          assertThat(response).isNotNull();

          assertThat(response.fullName())
              .isNotBlank()
              .isEqualTo(balance.fullName());

          assertThat(response.balance())
              .isNotNull()
              .isInstanceOf(BigDecimal.class)
              .isEqualTo(balance.balance());

          assertThat(response.dailyTransferLimit())
              .isNotNull()
              .isInstanceOf(BigDecimal.class)
              .isEqualTo(balance.dailyTransferLimit());

          assertThat(response.agency())
              .isNotNull()
              .isInstanceOf(Integer.class)
              .isEqualTo(balance.agency());

          assertThat(response.accountNumber())
              .isNotNull()
              .isInstanceOf(Integer.class)
              .isEqualTo(balance.accountNumber());
        });
  }

  @Test
  @DisplayName("Dada a request PATCH transfer-limits para atualizar o limite de transferencia diario "
      + "por um cliente valido retorne sucesso")
  public void test_patchTransferLimits_success() {
    final String customerId = UUID.randomUUID().toString();
    final PatchTransferLimitRequest transferLimitRequest = new PatchTransferLimitRequest(
        BigDecimal.valueOf(1000.00));
    final URI uri = URI.create("/accounts/".concat(customerId).concat("/transfer-limits"));

    when(accountService.updateTransferLimit(customerId, transferLimitRequest))
        .thenReturn(Mono.defer(() -> Mono.empty().then()));

    webTestClient.patch()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(transferLimitRequest), PatchTransferLimitRequest.class)
        .exchange()
        .expectStatus().isNoContent();
  }

  @Test
  @DisplayName("Dada a request POST tranfers para realizar uma transferencia bancaria retorne com "
      + "sucesso a transferencia solicitada.")
  public void test_postTransfers_success() {
    final String customerId = UUID.randomUUID().toString();
    final PostTransferRequest postTransferRequest = buildPostTransferRequest();
    final TransferResponse transferResponse = buildTransferResponse(postTransferRequest);
    final URI uri = URI.create("/accounts/".concat(customerId).concat("/transfers"));

    when(accountService.doTransfer(customerId, postTransferRequest))
        .thenReturn(Mono.just(transferResponse));

    webTestClient.post()
        .uri(uri)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(postTransferRequest), PostTransferRequest.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(TransferResponse.class)
        .value(response -> {
          assertThat(response)
              .isNotNull()
              .isInstanceOf(TransferResponse.class);

          assertThat(response.amount())
              .isNotNull()
              .isEqualTo(transferResponse.amount());

          assertThat(response.toAccount())
              .isNotNull()
              .isEqualTo(transferResponse.toAccount());

          assertThat(response.fromAccount())
              .isNotNull()
              .isEqualTo(transferResponse.fromAccount());

          assertThat(response.transferDate())
              .isNotBlank()
              .isEqualTo(transferResponse.transferDate());
        });
  }

  private TransferResponse buildTransferResponse(final PostTransferRequest request) {
    return new TransferResponse(
        new FromAccountResponse(4321, 7654321),
        new ToAccountResponse(request.toAccount().agency(), request.toAccount().accountNumber()),
        request.amount(),
        ApiUtils.getLocalDateTimeNow().toString()
    );
  }

  private PostTransferRequest buildPostTransferRequest() {
    return new PostTransferRequest(
        new ToAccountRequest(
            "Jose Nome Facil",
            123,
            1234567),
        BigDecimal.valueOf(100.0F));
  }

  private BalanceResponse buildBalanceResponse() {
    return new BalanceResponse(
        "Jose Nome Facil",
        123,
        1110997,
        BigDecimal.valueOf(1000.0F),
        BigDecimal.valueOf(1000.0F)
    );
  }
}
