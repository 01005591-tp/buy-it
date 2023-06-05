package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import pl.edu.pw.ee.pz.HttpApiError;
import pl.edu.pw.ee.pz.product.port.ProductNotFoundException;
import pl.edu.pw.ee.pz.shared.ProductDtoMapper;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

@RequiredArgsConstructor(access = PACKAGE)
class SearchProductByIdEndpoint {

  private final ProductQueryService productQueryService;
  private final ProductDtoMapper productDtoMapper;

  Uni<RestResponse<?>> handle(String id) {
    var productId = new ProductId(UUID.fromString(id));
    return productQueryService.searchByBasicCriteria(SearchProductQuery.byId(productId))
        .onItem().transform(pageResult -> pageResult.fold(
            RestResponse::notFound,
            product -> RestResponse.ok(new SearchProductByIdResponse(productDtoMapper.toProductDto(product))),
            products -> RestResponse.status(
                Status.INTERNAL_SERVER_ERROR,
                HttpApiError.ofCodeOnly(ProductErrorCode.SINGLE_PRODUCT_EXPECTED_BUT_MULTIPLE_FOUND)
            )
        ))
        .onFailure(ProductNotFoundException.class).recoverWithItem(RestResponse.notFound());
  }
}
