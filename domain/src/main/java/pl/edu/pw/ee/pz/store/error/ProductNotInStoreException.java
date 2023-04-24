package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;

public class ProductNotInStoreException extends RuntimeException {

  private ProductNotInStoreException(String message) {
    super(message);
  }

  public static ProductNotInStoreException notInStore(AggregateId productId) {
    return new ProductNotInStoreException("Product %s not in store".formatted(productId.value().toString()));
  }
}
