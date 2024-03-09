package andrelsf.github.com.mcaccounts.configs;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

  private final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

  private final String baseUrlBacen;
  private final String baseUrlCustomers;

  public WebClientConfig(
      @Value("${integrations.apis.baseUrlBacen}") String baseUrlBacen,
      @Value("${integrations.apis.baseUrlCustomers}") String baseUrlCustomers) {
    this.baseUrlBacen = baseUrlBacen;
    this.baseUrlCustomers = baseUrlCustomers;
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public WebClient.Builder webClientBuilder(ObjectProvider<WebClientCustomizer> customizerProvider) {
    WebClient.Builder webClientBuilder = WebClient.builder();
    customizerProvider.orderedStream()
        .forEach(customizer -> customizer.customize(webClientBuilder));
    return webClientBuilder.clientConnector(getClientConnector())
        .filters(exchangeFilterFunctions -> {
          exchangeFilterFunctions.add(logRequest());
          exchangeFilterFunctions.add(logResponse());
        })
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
  }

  @Bean
  public WebClient apiBacen(ObjectProvider<WebClientCustomizer> customizerProvider) {
    return webClientBuilder(customizerProvider)
        .baseUrl(baseUrlBacen)
        .build();
  }

  @Bean
  public WebClient apiCustomers(ObjectProvider<WebClientCustomizer> customizerProvider) {
    return webClientBuilder(customizerProvider)
        .baseUrl(baseUrlCustomers)
        .build();
  }

  public ClientHttpConnector getClientConnector() {
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000) // Connection Timeout
        .doOnConnected(connection -> connection
            .addHandlerLast(new ReadTimeoutHandler(30000, TimeUnit.MILLISECONDS)) // Read Timeout
            .addHandlerLast(new WriteTimeoutHandler(30000, TimeUnit.MILLISECONDS))); // Write Timeout
    return new ReactorClientHttpConnector(httpClient);
  }

  public ExchangeFilterFunction logRequest() {
    return (clientRequest, next) -> {
      logger.info("{} WebClient: {} {}", clientRequest.logPrefix(), clientRequest.method(),
          clientRequest.url());
      return next.exchange(clientRequest);
    };
  }

  public ExchangeFilterFunction logResponse() {
    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      var httpStatus = clientResponse.statusCode();
      if (httpStatus.isError()) {
        logger.error("{} Response: {}", clientResponse.logPrefix(), httpStatus);
      } else {
        logger.info("{} Response: {}", clientResponse.logPrefix(), httpStatus);
      }
      return Mono.just(clientResponse);
    });
  }
}
