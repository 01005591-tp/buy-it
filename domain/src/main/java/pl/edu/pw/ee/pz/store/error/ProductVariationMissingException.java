package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation.VariationId;

public class ProductVariationMissingException extends RuntimeException {

  private ProductVariationMissingException(String message) {
    super(message);
  }

  public static ProductVariationMissingException variationMissing(AggregateId productId, VariationId variation) {
    return new ProductVariationMissingException(
        "Variation %s not defined for product %s".formatted(variation.value().toString(), productId.value().toString())
    );
  }
}
