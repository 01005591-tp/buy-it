package pl.edu.pw.ee.pz.store;

import java.util.List;
import pl.edu.pw.ee.pz.shared.ProductDto;

record GetProductsWithAvailabilityResponse(
    List<ProductDto> products
) {

}
