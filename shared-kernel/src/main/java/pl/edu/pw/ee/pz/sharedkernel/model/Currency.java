package pl.edu.pw.ee.pz.sharedkernel.model;

public record Currency(
    CurrencyId id,
    CurrencyName name
) {

  public record CurrencyId(String value) {

  }

  public record CurrencyName(String name) {

  }

}
