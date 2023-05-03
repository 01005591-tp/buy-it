package pl.edu.pw.ee.pz.product;

import java.util.List;

record UpdateProductRequest(
    String code,
    String brandId,
    List<Variation> variations
) {

}
