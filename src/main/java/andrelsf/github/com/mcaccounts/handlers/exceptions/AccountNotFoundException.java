package andrelsf.github.com.mcaccounts.handlers.exceptions;

public class AccountNotFoundException extends RuntimeException {

  public AccountNotFoundException(String message) {
    super(message);
  }
}
