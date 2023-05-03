package pl.edu.pw.ee.pz.sharedkernel.model;

import java.util.Set;

public record ProductVariation(
    Set<VariationAttribute> attributes
) {
}
