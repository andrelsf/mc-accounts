package andrelsf.github.com.mcaccounts.services.impl.queues;

import andrelsf.github.com.mcaccounts.entities.events.StatusMessage;
import andrelsf.github.com.mcaccounts.entities.events.InputMessage;
import andrelsf.github.com.mcaccounts.entities.events.QueueType;
import andrelsf.github.com.mcaccounts.services.QueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class QueueServiceImpl implements QueueService {

  private final static Logger logger = LoggerFactory.getLogger(QueueServiceImpl.class);

  @Override
  public Mono<Void> send(StatusMessage status, InputMessage message, QueueType queueType) {
    logger.warn("Implement method to insert into queue");
    return Mono.empty();
  }
}
