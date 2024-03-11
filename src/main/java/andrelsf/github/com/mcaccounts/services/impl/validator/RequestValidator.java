package andrelsf.github.com.mcaccounts.services.impl.validator;

import andrelsf.github.com.mcaccounts.handlers.exceptions.InputError;
import andrelsf.github.com.mcaccounts.handlers.exceptions.InputException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RequestValidator {

  private static final Logger logger = LoggerFactory.getLogger(RequestValidator.class);
  private final Validator validator;

  public RequestValidator(Validator validator) {
    this.validator = validator;
  }

  public <T> void validate(T validationData) {
    final Errors errors = new BeanPropertyBindingResult(validationData, validationData.getClass().getName());
    validator.validate(validationData, errors);
    if (errors.hasErrors()) {
      List<InputError> inputErrors = new ArrayList<>();
      for (int index = 0; errors.getErrorCount() > index; index++) {
        String fieldError = errors.getFieldErrors().get(index).getField();
        String errorMessage = errors.getAllErrors().get(index).getDefaultMessage();
        logger.error("Field: {} : Message: {}", fieldError, errorMessage);
        inputErrors.add(new InputError(fieldError, errorMessage));
      }
      throw new InputException("Invalid request payload", inputErrors);
    }
  }
}
