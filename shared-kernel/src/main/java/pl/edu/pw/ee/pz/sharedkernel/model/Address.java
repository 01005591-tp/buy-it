package pl.edu.pw.ee.pz.sharedkernel.model;

public record Address(
    Street street,
    City city,
    ZipCode zipCode,
    Country country
) {

  public record Street(
      StreetName name,
      HouseNo house,
      FlatNo flatNo
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
