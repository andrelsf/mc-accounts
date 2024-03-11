package andrelsf.github.com.mcaccounts.configs;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiErrorHttpStatusException {

  @Bean
  public WebProperties.Resources webPropertiesResources() {
    return new WebProperties.Resources();
  }

}
