package andrelsf.github.com.mcaccounts.entities.events;

import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;

public record InputMessage(
    Object clazz,
    String className
) {

  public static InputMessage of(final TransferResponse transferResponse) {
    return new InputMessage(transferResponse, transferResponse.getClass().getSimpleName());
  }
}
