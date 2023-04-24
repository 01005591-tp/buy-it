package pl.edu.pw.ee.pz.product;

import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;

public record NewProductCommand(
    ProductCode code,
    BrandCode brand,
    List<NewProductVariation> variations
) implements Command {

  public NewProductCommand(ProductCode code, BrandCode brand) {
    this(code, brand, List.of());
  }
}
