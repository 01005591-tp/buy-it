package pl.edu.pw.ee.pz.sharedkernel.model;

import io.vavr.control.Option;

public record Address(
    Option<Street> street,
    Option<City> city,
    Option<ZipCode> zipCode,
    Country country
) {

  public Address(Street street, City city, ZipCode zipCode, Country country) {
    this(
        Option.of(street),
        Option.of(city),
        Option.of(zipCode),
        country
    );
  }

  public record Street(
      StreetName name,
      HouseNo house,
      Option<FlatNo> flatNo
  ) {

    public Street(StreetName name, HouseNo house) {
      this(name, house, null);
    }
  }

  public record City(String value) {

  }

  public record ZipCode(String value) {

  }

  public record StreetName(String value) {

  }

  public record HouseNo(String value) {

  }

  public record FlatNo(String value) {

  }
}
