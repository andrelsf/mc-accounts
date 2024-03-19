package andrelsf.github.com.mcaccounts.configs;

import andrelsf.github.com.mcaccounts.api.http.requests.PatchTransferLimitRequest;
import andrelsf.github.com.mcaccounts.api.http.requests.PostTransferRequest;
import andrelsf.github.com.mcaccounts.api.http.responses.BalanceResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.CustomerResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.FromAccountResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.ToAccountResponse;
import andrelsf.github.com.mcaccounts.api.http.responses.TransferResponse;
import andrelsf.github.com.mcaccounts.handlers.exceptions.ApiError;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import java.time.format.DateTimeFormatter;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@RegisterReflectionForBinding(
    classes = {
      ApiError.class,
      PatchTransferLimitRequest.class,
      PostTransferRequest.class,
      BalanceResponse.class,
      CustomerResponse.class,
      TransferResponse.class,
      FromAccountResponse.class,
      ToAccountResponse.class
    }
)
public class AppConfig {

  @Bean
  public Jackson2ObjectMapperBuilder objectMapperBuilder() {
    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder.serializationInclusion(JsonInclude.Include.NON_NULL);
    builder.dateFormat(new StdDateFormat().withColonInTimeZone(true));
    builder.serializers(
        new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")),
        new ZonedDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
    );
    return builder;
  }

}
