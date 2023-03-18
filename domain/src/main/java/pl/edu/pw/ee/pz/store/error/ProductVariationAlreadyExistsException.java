package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.store.ProductVariation.VariationId;

public class ProductVariationAlreadyExistsException extends RuntimeException {

  private ProductVariationAlreadyExistsException(String message) {
    super(message);
  }

  public static ProductVariationAlreadyExistsException alreadyExists(VariationId variation) {
    return new ProductVariationAlreadyExistsException(
        "Product %s already exists".formatted(variation.value().toString())
    );
  }

}
