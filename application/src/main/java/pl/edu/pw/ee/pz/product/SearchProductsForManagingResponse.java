package pl.edu.pw.ee.pz.product;

import pl.edu.pw.ee.pz.shared.ProductDto;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;

record SearchProductsForManagingResponse(
    PageResult<ProductDto> products
) {

}
