package pl.edu.pw.ee.pz.product;

import java.util.Set;
import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

public record RemoveProductVariationsCommand(
    ProductId product,
    Set<ProductVariation> variations
) implements Command {

}
