package pl.edu.pw.ee.pz.brand.error;

import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;

public class InvalidBrandCodeException extends RuntimeException {

  private InvalidBrandCodeException(String message) {
    super(message);
  }

  public static InvalidBrandCodeException sameCode(BrandCode code) {
    return new InvalidBrandCodeException("Brand code already has the expected code: %s".formatted(code.value()));
  }

  public static InvalidBrandCodeException emptyCode(BrandCode code) {
    return new InvalidBrandCodeException(
        "Brand code cannot be null, blank or empty. Received %s".formatted(code.value())
    );
  }
}
