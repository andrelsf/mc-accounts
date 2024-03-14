package andrelsf.github.com.mcaccounts.entities.events;

import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import java.util.UUID;

public record InputMessage(
    String messageId,
    Object clazz,
    String className
) {

  public static InputMessage of(final TransferResponse transferResponse) {
    return new InputMessage(
        UUID.randomUUID().toString(), transferResponse, transferResponse.getClass().getSimpleName());
  }
}
