package pl.edu.pw.ee.pz.sharedkernel.model;

import java.util.Set;

@SuppressWarnings("rawtypes")
public record ProductVariation(
    ProductVariationId id,
    Set<VariationAttribute> attributes
) {
}
