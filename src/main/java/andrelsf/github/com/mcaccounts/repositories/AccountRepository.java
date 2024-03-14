package andrelsf.github.com.mcaccounts.repositories;


import andrelsf.github.com.mcaccounts.entities.domains.AccountEntity;
import java.math.BigDecimal;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<AccountEntity, String> {

  Mono<AccountEntity> findAccountEntityByCustomerIdAndStatusIs(final String customerId, final String status);
  Mono<AccountEntity> findAccountEntityByAgencyAndAccountNumberAndStatusIs(
      final Integer agency, final Integer accountNumber, final String status);
  Mono<AccountEntity> findAccountEntityByCustomerIdAndBalanceGreaterThanEqualAndDailyTransferLimitGreaterThanEqualAndStatusIs(
      final String customerId, final BigDecimal amountBalance, final BigDecimal amountDailyTransferLimit, final String status);

}
