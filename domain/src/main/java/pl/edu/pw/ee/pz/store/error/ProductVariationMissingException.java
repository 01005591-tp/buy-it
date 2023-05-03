package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

public class ProductVariationMissingException extends RuntimeException {

  private ProductVariationMissingException(String message) {
    super(message);
  }

  public static ProductVariationMissingException variationMissing(AggregateId productId, ProductVariation variation) {
    return new ProductVariationMissingException(
        "Variation not defined for product %s: %s".formatted(productId.value(), variation.toString())
    );
  }
}
