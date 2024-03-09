package andrelsf.github.com.mcaccounts.utils;

import andrelsf.github.com.mcaccounts.api.http.responses.BalanceResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.CustomerResponse;
import andrelsf.github.com.mcaccounts.entities.AccountEntity;

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
}
