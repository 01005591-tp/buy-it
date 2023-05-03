package pl.edu.pw.ee.pz.product.port;

import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

public class ProductNotFoundException extends RuntimeException {

  private ProductNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public static ProductNotFoundException notFound(ProductId id, Throwable cause) {
    return new ProductNotFoundException("Product %s not found".formatted(id.value()), cause);
  }
}
