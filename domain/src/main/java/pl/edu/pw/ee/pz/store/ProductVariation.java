package pl.edu.pw.ee.pz.store;

import java.util.Set;
import java.util.UUID;
import pl.edu.pw.ee.pz.store.Product.ProductId;

public record ProductVariation(
    VariationId id,
    ProductId product,
    Set<VariationAttribute> attributes
) {

  public record VariationId(UUID value) {

  }
}
