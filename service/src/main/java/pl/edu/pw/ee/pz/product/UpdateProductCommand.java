package pl.edu.pw.ee.pz.product;

import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

public record UpdateProductCommand(
    ProductId id,
    ProductCode code,
    BrandCode brand,
    List<NewProductVariation> variations
) implements Command {

}
