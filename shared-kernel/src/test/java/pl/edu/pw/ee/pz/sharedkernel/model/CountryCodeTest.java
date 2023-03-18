package pl.edu.pw.ee.pz.sharedkernel.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.edu.pw.ee.pz.sharedkernel.model.CountryCode.InvalidCountryCodeException;

class CountryCodeTest {

  @Test
  void should_instantiate_valid_country() {
    // given
    var code = "PL";

    // when
    var countryCode = CountryCode.of("PL");

    // then
    assertThat(countryCode.value()).isEqualTo(code);
  }

  @Test
  void should_fail_instantiating_country_on_missing_code() {
    // when
    var throwableAssert = assertThatCode(
        () -> CountryCode.of(null)
    );

    // then
    throwableAssert
        .isInstanceOf(InvalidCountryCodeException.class)
        .hasMessage("Missing country code");
  }

  @CsvSource(value = {
      "' '",
      "'\n'",
      "''",
      "POL",
      "XX",
  })
  @ParameterizedTest(name = "[{index}] code = {0}")
  void should_fail_instantiating_country_on_invalid_country_code(String code) {
    // when
    var throwableAssert = assertThatCode(
        () -> CountryCode.of(code)
    );

    // then
    throwableAssert
        .isInstanceOf(InvalidCountryCodeException.class)
        .hasMessage("Invalid country code: " + code);
  }

  @Test
  void should_have_iso_countries_initialized() {
    // then
    assertThat(CountryCode.ISO_COUNTRIES)
        .hasSize(249);
  }
}