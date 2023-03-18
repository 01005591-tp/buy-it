package pl.edu.pw.ee.pz.sharedkernel.model;

import java.util.Locale;
import java.util.Locale.IsoCountryCode;
import java.util.Set;
import java.util.stream.Collectors;

public interface CountryCodeProvider {

  /**
   * Returns known ISO3166-1 alpha-2 country codes.
   *
   * @return known ISO3166-1 alpha-2 country codes.
   */
  Set<String> countryCodes();

  class JdkCountryCodeProvider implements CountryCodeProvider {

    @Override
    public Set<String> countryCodes() {
      return Locale.getISOCountries(IsoCountryCode.PART1_ALPHA2).stream()
          .collect(Collectors.toUnmodifiableSet());
    }
  }
}
