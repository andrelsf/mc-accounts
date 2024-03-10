package andrelsf.github.com.mcaccounts.api.http.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PostTransferRequest(
    @NotNull @Valid ToAccountRequest toAccount,
    @NotNull BigDecimal amount
) {

}
