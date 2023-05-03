package pl.edu.pw.ee.pz.store;

import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

public record ProductAvailability(
    ProductVariation productVariation,
    AvailableCount available
) {

  public record AvailableCount(Long value) {

  }
}
