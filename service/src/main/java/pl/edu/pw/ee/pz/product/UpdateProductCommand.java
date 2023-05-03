package pl.edu.pw.ee.pz.product;

import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

public record UpdateProductCommand(
    ProductId id,
    ProductCode code,
    BrandId brand,
    List<ProductVariation> variations
) implements Command {

}
