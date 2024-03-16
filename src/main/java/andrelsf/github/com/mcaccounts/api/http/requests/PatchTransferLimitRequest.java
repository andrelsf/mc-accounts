package andrelsf.github.com.mcaccounts.api.http.requests;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PatchTransferLimitRequest(
    @NotNull BigDecimal amount
) {}
