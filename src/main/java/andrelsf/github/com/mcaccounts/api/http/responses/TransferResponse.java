package andrelsf.github.com.mcaccounts.api.http.responses;

import java.math.BigDecimal;

public record TransferResponse(
    FromAccountResponse fromAccount,
    ToAccountResponse toAccount,
    BigDecimal amount,
    String transferDate
) {

}
