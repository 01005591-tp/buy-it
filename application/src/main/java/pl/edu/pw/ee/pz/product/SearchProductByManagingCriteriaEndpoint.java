package pl.edu.pw.ee.pz.product;

import static java.util.Objects.requireNonNullElse;
import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vavr.control.Option;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;
import pl.edu.pw.ee.pz.shared.ProductDtoMapper;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;
import pl.edu.pw.ee.pz.sharedkernel.query.RequestedPage;

@RequiredArgsConstructor(access = PACKAGE)
class SearchProductByManagingCriteriaEndpoint {

  // TODO: add product query page size configuration
  private static final long DEFAULT_PAGE_SIZE = 25L;
  private static final long DEFAULT_KEY_SET_ITEM_ID = 0L;

  private final ProductQueryService productQueryService;
  private final ProductDtoMapper productDtoMapper;

  Uni<RestResponse<?>> handle(SearchProductForManagingRequest request) {
    var productCode = Option.of(request.code)
        .map(ProductCode::new);
    var brandId = Option.of(request.brandId)
        .filter(Predicate.not(String::isBlank))
        .map(it -> new BrandId(UUID.fromString(it)));
    return productQueryService.searchByBasicCriteria(
            SearchProductQuery.byBasicCriteria(
                productCode,
                brandId,
                new RequestedPage(
                    requireNonNullElse(request.pageSize, DEFAULT_PAGE_SIZE),
                    requireNonNullElse(request.keySetItemId, DEFAULT_KEY_SET_ITEM_ID)
                )
            )
        )
        .onItem().transform(pageResult ->
            pageResult.transform(
                empty -> RestResponse.noContent(),
                this::toResponseWithContent,
                this::toResponseWithContent
            )
        );
  }

  private RestResponse<SearchProductsForManagingResponse> toResponseWithContent(PageResult<Product> multi) {
    return RestResponse.ok(
        new SearchProductsForManagingResponse(multi.map(productDtoMapper::toProductDto))
    );
  }
}
