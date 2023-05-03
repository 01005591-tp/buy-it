package pl.edu.pw.ee.pz.product;

import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

public record NewProductVariationsCommand(
    ProductId product,
    List<ProductVariation> variations
) implements Command {

}
