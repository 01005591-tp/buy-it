package pl.edu.pw.ee.pz.product;

import java.util.List;
import pl.edu.pw.ee.pz.shared.Variation;

record UpdateProductRequest(
    String code,
    String brandId,
    List<Variation> variations
) {

}
