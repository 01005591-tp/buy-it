package pl.edu.pw.ee.pz.sharedkernel.model;

import java.util.Set;

public record ProductVariation(
    ProductVariationId id,
    Set<VariationAttribute> attributes
) {
}
