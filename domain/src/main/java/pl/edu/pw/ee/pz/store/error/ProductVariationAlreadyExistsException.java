package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation.VariationId;

public class ProductVariationAlreadyExistsException extends RuntimeException {

  private ProductVariationAlreadyExistsException(String message) {
    super(message);
  }

  public static ProductVariationAlreadyExistsException alreadyExists(AggregateId productId, VariationId variation) {
    return new ProductVariationAlreadyExistsException("Product variation %s already exists for product %s.".formatted(
        variation.value().toString(), productId.value().toString()
    ));
  }

}
