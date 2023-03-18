package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.store.Product.ProductId;

public class ProductAlreadyExistsException extends RuntimeException {

  private ProductAlreadyExistsException(String message) {
    super(message);
  }

  public static ProductAlreadyExistsException alreadyExists(ProductId id) {
    return new ProductAlreadyExistsException(
        "Product %s already exists".formatted(id.value().toString())
    );
  }
}
