package andrelsf.github.com.mcaccounts.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.server.ServerWebInputException;

@Component
public class RequestValidator {

  private static final Logger logger = LoggerFactory.getLogger(RequestValidator.class);
  private final Validator validator;

  public RequestValidator(Validator validator) {
    this.validator = validator;
  }

  public <T> void validate(T validationData) {
    Errors errors = new BeanPropertyBindingResult(validationData, validationData.getClass().getName());
    validator.validate(validationData, errors);
    if (errors.hasErrors()) {
      logger.error(errors.toString());
      throw new ServerWebInputException(errors.toString());
    }
  }
}
