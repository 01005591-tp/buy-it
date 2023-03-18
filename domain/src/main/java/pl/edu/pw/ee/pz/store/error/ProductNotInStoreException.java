package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.store.Product.ProductId;

public class ProductNotInStoreException extends RuntimeException {

  private ProductNotInStoreException(String message) {
    super(message);
  }

  public static ProductNotInStoreException notInStore(ProductId productId) {
    return new ProductNotInStoreException("Product %s not in store".formatted(productId.value().toString()));
  }
}
