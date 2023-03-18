package pl.edu.pw.ee.pz.sharedkernel.model;

import java.math.BigDecimal;

public record Price(
    Currency currency,
    Amount amount
) {

  public record Amount(BigDecimal value) {

  }
}
