package andrelsf.github.com.mcaccounts.api.http.responses;

import java.math.BigDecimal;

public record BalanceResponse(
  String fullName,
  Integer agency,
  Integer accountNumber,
  BigDecimal dailyTransferLimit,
  BigDecimal balance
) {}
