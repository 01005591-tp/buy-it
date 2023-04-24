package pl.edu.pw.ee.pz.sharedkernel.model;

import java.util.Set;
import java.util.UUID;

public record ProductVariation(
    VariationId id,
    Set<VariationAttribute> attributes
) {

  public record VariationId(UUID value) {

  }
}
