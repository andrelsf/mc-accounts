package andrelsf.github.com.mcaccounts.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApiUtils {

  private ApiUtils() {}

  public static LocalDateTime getLocalDateTimeNow() {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    final String localDateTimeNow = LocalDateTime.now().format(formatter);
    return LocalDateTime.parse(localDateTimeNow, formatter);
  }
}
