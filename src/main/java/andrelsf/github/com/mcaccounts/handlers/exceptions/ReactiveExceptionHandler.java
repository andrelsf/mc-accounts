package andrelsf.github.com.mcaccounts.handlers.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Order(-2)
@Component
public class ReactiveExceptionHandler extends AbstractErrorWebExceptionHandler {

  private final static Logger logger = LoggerFactory.getLogger(ReactiveExceptionHandler.class);
  private final static HttpStatus httpStatusDefault = HttpStatus.INTERNAL_SERVER_ERROR;

  public ReactiveExceptionHandler(
      ErrorAttributes errorAttributes,
      WebProperties.Resources resources,
      ServerCodecConfigurer serverCodecConfigurer,
      ApplicationContext applicationContext) {
    super(errorAttributes, resources, applicationContext);
    super.setMessageWriters(serverCodecConfigurer.getWriters());
    super.setMessageReaders(serverCodecConfigurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::errorResponse);
  }

  private Mono<ServerResponse> errorResponse(ServerRequest request) {
    final Throwable error = getError(request);
    logger.error("Error captured", error);
    if (error instanceof InputException) {
      return getServerResponse(HttpStatus.BAD_REQUEST, ((InputException) error).getErrors());
    }

    return getServerResponse(httpStatusDefault, new ApiError(httpStatusDefault.value(), error.getMessage()));
  }

  private Mono<ServerResponse> getServerResponse(final HttpStatus httpStatus, final Object body) {
    return ServerResponse.status(httpStatus)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(body));
  }
}
