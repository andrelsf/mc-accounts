package andrelsf.github.com.mcaccounts.services;

import andrelsf.github.com.mcaccounts.entities.events.StatusMessage;
import andrelsf.github.com.mcaccounts.entities.events.InputMessage;
import andrelsf.github.com.mcaccounts.entities.events.QueueType;
import reactor.core.publisher.Mono;

public interface QueueService {

  Mono<Void> send(StatusMessage status, InputMessage message, QueueType queueType);

}
