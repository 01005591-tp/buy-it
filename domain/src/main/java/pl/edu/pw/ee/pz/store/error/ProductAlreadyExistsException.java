package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;

public class ProductAlreadyExistsException extends RuntimeException {

  private ProductAlreadyExistsException(String message) {
    super(message);
  }

  public static ProductAlreadyExistsException alreadyExists(AggregateId id) {
    return new ProductAlreadyExistsException(
        "Product %s already exists".formatted(id.value().toString())
    );
  }
}
