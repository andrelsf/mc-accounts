package andrelsf.github.com.mcaccounts.handlers.exceptions;

import java.util.List;

public class InputException extends RuntimeException {

  private final List<InputError> errors;
  public InputException(String message, List<InputError> errors) {
    super(message);
    this.errors = errors;
  }

  public List<InputError> getErrors() {
    return errors;
  }
}
