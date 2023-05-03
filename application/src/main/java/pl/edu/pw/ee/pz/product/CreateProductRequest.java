package pl.edu.pw.ee.pz.product;

import java.util.List;

record CreateProductRequest(
    String code,
    String brandId,
    List<Variation> variations
) {

}
