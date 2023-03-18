package pl.edu.pw.ee.pz.sharedkernel.model;

public record Country(CountryCode code) {

  public static final Country PL = new Country(CountryCode.ISO_COUNTRIES.get("PL"));
}
