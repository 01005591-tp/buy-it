package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.store.Product.ProductId;
import pl.edu.pw.ee.pz.store.ProductVariation.VariationId;

public class ProductVariationUndefinedException extends RuntimeException {

  private ProductVariationUndefinedException(String message) {
    super(message);
  }

  public static ProductVariationUndefinedException variationUndefined(ProductId product, VariationId variation) {
    return new ProductVariationUndefinedException(
        "Variation %s not defined for product %s".formatted(variation.value().toString(), product.value().toString())
    );
  }
}
