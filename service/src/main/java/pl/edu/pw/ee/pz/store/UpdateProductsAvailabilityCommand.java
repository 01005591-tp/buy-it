package pl.edu.pw.ee.pz.store;

import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationPieces;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

public record UpdateProductsAvailabilityCommand(
    StoreId store,
    List<ProductVariationPieces> productsPieces
) implements Command {

}
