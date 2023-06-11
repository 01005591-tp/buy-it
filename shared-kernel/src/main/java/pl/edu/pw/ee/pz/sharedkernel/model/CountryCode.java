package pl.edu.pw.ee.pz.sharedkernel.model;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.sharedkernel.model.CountryCodeProvider.JdkCountryCodeProvider;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = PRIVATE)
public class CountryCode {

  static final Map<String, CountryCode> ISO_COUNTRIES;

  static {
    var loader = ServiceLoader.load(CountryCodeProvider.class);
    var countryCodeProvider = loader.findFirst()
        .orElseGet(JdkCountryCodeProvider::new);
    ISO_COUNTRIES = countryCodeProvider.countryCodes().stream()
        .collect(toMap(Function.identity(), CountryCode::new));
  }

  @JsonProperty
  @Getter
  @Accessors(fluent = true)
  private final String value;

  @JsonCreator(mode = Mode.PROPERTIES)
  public static CountryCode of(String value) {
    if (isNull(value)) {
      throw InvalidCountryCodeException.missingCode();
    }
    var countryCode = ISO_COUNTRIES.get(value);
    if (isNull(countryCode)) {
      throw InvalidCountryCodeException.invalidCode(value);
    }
    return countryCode;
  }

  public static class InvalidCountryCodeException extends RuntimeException {

    private InvalidCountryCodeException(String message) {
      super(message);
    }

    private static InvalidCountryCodeException missingCode() {
      return new InvalidCountryCodeException("Missing country code");
    }

    private static InvalidCountryCodeException invalidCode(String code) {
      return new InvalidCountryCodeException("Invalid country code: %s".formatted(code));
    }
  }
}


