package andrelsf.github.com.mcaccounts.services;

import andrelsf.github.com.mcaccounts.api.http.responses.BalanceResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.CustomerResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface CustomerService {

  Mono<BalanceResponse> checkAccountBalance(String customerId);

}
