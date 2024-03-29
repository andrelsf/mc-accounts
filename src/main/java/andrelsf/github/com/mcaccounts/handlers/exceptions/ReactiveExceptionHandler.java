package andrelsf.github.com.mcaccounts.handlers.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

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
    return switch (error.getClass().getSimpleName()) {
      case "AccountNotFoundException" ->
          getServerResponse(NOT_FOUND, new ApiError(NOT_FOUND.value(), error.getMessage()));
      case "InputException" ->
          getServerResponse(BAD_REQUEST, ((InputException) error).getErrors());
      case "UnableToTransfer",
          "ToAccountNotFoundException" ->
          getServerResponse(UNPROCESSABLE_ENTITY, new ApiError(UNPROCESSABLE_ENTITY.value(), error.getMessage()));
      default ->
          getServerResponse(httpStatusDefault, new ApiError(httpStatusDefault.value(), "Contact Sysadmin."));
    };
  }

  private Mono<ServerResponse> getServerResponse(final HttpStatus httpStatus, final Object body) {
    return ServerResponse.status(httpStatus)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(body));
  }
}
