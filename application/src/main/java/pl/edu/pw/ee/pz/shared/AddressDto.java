package pl.edu.pw.ee.pz.shared;

import io.vavr.control.Option;

public record AddressDto(
    Option<StreetDto> street,
    Option<String> city,
    Option<String> zipCode,
    String country
) {

  public record StreetDto(
      String name,
      String house,
      Option<String> flatNo
  ) {

  }
}
