package pl.edu.pw.ee.pz.product;

import java.util.Set;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

public record Product(
    ProductId id,
    ProductCode code,
    BrandId brand,
    Set<ProductVariation> variations
) {

  public Product(ProductId id, ProductCode code, BrandId brand) {
    this(id, code, brand, Set.of());
  }
}
