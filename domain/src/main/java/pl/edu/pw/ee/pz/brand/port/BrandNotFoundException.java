package pl.edu.pw.ee.pz.brand.port;

import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;

public class BrandNotFoundException extends RuntimeException {

  private BrandNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public static BrandNotFoundException notFound(BrandId brandId, Throwable cause) {
    return new BrandNotFoundException("Brand %s not found".formatted(brandId.value()), cause);
  }
}
