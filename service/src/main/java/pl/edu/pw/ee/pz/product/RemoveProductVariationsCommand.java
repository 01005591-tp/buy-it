package pl.edu.pw.ee.pz.product;

import java.util.Set;
import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation.VariationId;

public record RemoveProductVariationsCommand(
    ProductId product,
    Set<VariationId> variations
) implements Command {

}
