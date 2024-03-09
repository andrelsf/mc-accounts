package andrelsf.github.com.mcaccounts.repositories;


import andrelsf.github.com.mcaccounts.entities.AccountEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveSortingRepository<AccountEntity, String> {

  Mono<AccountEntity> findAccountEntityByCustomerIdAndStatusIs(final String customerId, final String status);

}
