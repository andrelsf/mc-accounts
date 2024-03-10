package andrelsf.github.com.mcaccounts.services;

import andrelsf.github.com.mcaccounts.api.http.requests.PostTransferRequest;
import andrelsf.github.com.mcaccounts.api.http.responses.BalanceResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface CustomerService {

  Mono<BalanceResponse> checkAccountBalance(final String customerId);
  Mono<TransferResponse> doTransfer(final String customerId, final PostTransferRequest postTransferRequest);

}
