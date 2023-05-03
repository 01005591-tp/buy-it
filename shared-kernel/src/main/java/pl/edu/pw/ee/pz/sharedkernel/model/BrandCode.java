package pl.edu.pw.ee.pz.sharedkernel.model;

import static java.util.Objects.isNull;

public record BrandCode(String value) {

  public boolean isNullOrBlank() {
    return isNull(value) || value.isBlank();
  }
}
