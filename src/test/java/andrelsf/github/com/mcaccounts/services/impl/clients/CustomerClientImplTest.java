package andrelsf.github.com.mcaccounts.services.impl.clients;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import andrelsf.github.com.mcaccounts.api.http.responses.CustomerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
public class CustomerClientImplTest {

  private static MockWebServer mockWebServer;

  private WebClient webClient;

  private CustomerClientImpl customerClient;

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
    customerClient = new CustomerClientImpl(webClient);
  }

  @Test
  @DisplayName("Dado o customerId valido consulte na API e returne o customer com sucesso.")
  void test_getCustomerById_success() throws JsonProcessingException {
    final String customerId = UUID.randomUUID().toString();
    final ObjectMapper objectMapper = new ObjectMapper();
    final CustomerResponse customerResponseMock = buildCustomerResponse(customerId);

    mockWebServer.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(customerResponseMock))
        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

    final CustomerResponse customerResponse = customerClient.getCustomerById(customerId).block();

    assertThat(customerResponse)
        .isNotNull()
        .isInstanceOf(CustomerResponse.class);

    assertThat(customerResponse.customerId())
        .isNotBlank()
        .isEqualTo(customerId);

    assertThat(customerResponse.fullName())
        .isNotBlank()
        .isEqualTo("Jose Nome Facil");

    assertThat(customerResponse.cpf())
        .isNotBlank()
        .isEqualTo("11122233344");
  }

  private CustomerResponse buildCustomerResponse(final String customerId) {
    return new CustomerResponse(customerId, "Jose Nome Facil", "11122233344");
  }

}
