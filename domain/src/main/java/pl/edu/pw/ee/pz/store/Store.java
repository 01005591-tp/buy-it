package pl.edu.pw.ee.pz.store;

import java.util.Map;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;
import pl.edu.pw.ee.pz.sharedkernel.model.Pieces;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreCode;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

public record Store(
    StoreId id,
    StoreCode code,
    Address address,
    Map<ProductId, Map<ProductVariationId, Pieces>> products
) {

}
