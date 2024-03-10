package andrelsf.github.com.mcaccounts.utils;

import andrelsf.github.com.mcaccounts.api.http.responses.BalanceResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.CustomerResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.FromAccountResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.ToAccountResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import andrelsf.github.com.mcaccounts.entities.AccountEntity;
import java.math.BigDecimal;

public class Mapper {

  private Mapper() {}

  public static BalanceResponse entityToBalanceResponse(
      final AccountEntity accountEntity, final CustomerResponse customerResponse) {
    return new BalanceResponse(
      customerResponse.fullName(),
      accountEntity.getAgency(),
      accountEntity.getAccountNumber(),
      accountEntity.getDailyTransferLimit(),
      accountEntity.getBalance()
    );
  }

  public static TransferResponse entitiesToTransferResponse(
      final String transactionId,
      final AccountEntity fromAccount,
      final AccountEntity toAccount,
      final BigDecimal amount,
      final String transferDate) {
    return new TransferResponse(
        transactionId,
        new FromAccountResponse(fromAccount.getAgency(), fromAccount.getAccountNumber()),
        new ToAccountResponse(toAccount.getAgency(), toAccount.getAccountNumber()),
        amount,
        transferDate);
  }
}
