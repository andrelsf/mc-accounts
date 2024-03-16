package andrelsf.github.com.mcaccounts.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import andrelsf.github.com.mcaccounts.api.http.requests.PostTransferRequest;
import andrelsf.github.com.mcaccounts.api.http.requests.ToAccountRequest;
import andrelsf.github.com.mcaccounts.services.impl.validator.RequestValidator;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
public class RequestValidatorTest {


  private RequestValidator requestValidator;

  @BeforeEach
  void initialize() {
    final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    requestValidator = new RequestValidator(validator);
  }

  @Test
  @DisplayName("Dada a request de trasacao bancaria que a validacao seja com sucesso")
  void test_validate_success() {
    final PostTransferRequest request = buildTransferRequest();

    assertDoesNotThrow(() -> requestValidator.validate(request));
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
}
