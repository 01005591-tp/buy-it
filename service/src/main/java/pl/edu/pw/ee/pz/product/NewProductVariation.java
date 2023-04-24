package pl.edu.pw.ee.pz.product;

import java.util.Set;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute;

public record NewProductVariation(
    Set<VariationAttribute> attributes
) {

}
