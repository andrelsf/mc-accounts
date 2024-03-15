package andrelsf.github.com.mcaccounts.services.impl.clients;

import static andrelsf.github.com.mcaccounts.entities.domains.AccountStatus.ACTIVE;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import andrelsf.github.com.mcaccounts.api.http.requests.PostTransferRequest;
import andrelsf.github.com.mcaccounts.api.http.requests.ToAccountRequest;
import andrelsf.github.com.mcaccounts.api.http.responses.FromAccountResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.ToAccountResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import andrelsf.github.com.mcaccounts.entities.domains.AccountEntity;
import andrelsf.github.com.mcaccounts.utils.ApiUtils;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
public class BacenClientImplTest {

  private static MockWebServer mockWebServer;

  private WebClient webClient;

  private ReactiveCircuitBreaker apiBacenCircuitBreaker;

  private BacenClientImpl bacenClient;

  @BeforeAll
  static void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @BeforeEach
  void initialize() {
    final String baseUrl = format("http://localhost:%s", mockWebServer.getPort());

    webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build();

    final CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
    final TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.ofDefaults();
    final Resilience4JConfigurationProperties properties = new Resilience4JConfigurationProperties();

    apiBacenCircuitBreaker =
        new ReactiveResilience4JCircuitBreakerFactory(registry, timeLimiterRegistry, properties)
        .create("testCB");

    bacenClient = new BacenClientImpl(webClient, apiBacenCircuitBreaker);
  }


  @Test
  @DisplayName("Dada uma transferencia realizada deve notificar BACEN em sua API.")
  void test_postNotification_success() {
    final String customerId = UUID.randomUUID().toString();
    final PostTransferRequest request = buildTransferRequest();
    final AccountEntity fromAccount = buildAccountEntity(customerId);
    final AccountEntity toAccount = buildToAccountEntity(request);
    final TransferResponse transferResponse = buildTransferResponse(fromAccount, toAccount, request.amount());

    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(HttpStatus.OK.value())
        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

    assertDoesNotThrow(() -> bacenClient.postNotification(transferResponse).block());
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
            "22233344455",
            321,
            7654321),
        BigDecimal.valueOf(100.00f)
    );
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
