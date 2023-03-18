package pl.edu.pw.ee.pz.store;

import pl.edu.pw.ee.pz.store.ProductVariation.VariationId;

public record ProductAvailability(
    VariationId productVariation,
    AvailableCount available
) {

  public record AvailableCount(Long value) {

  }
}
