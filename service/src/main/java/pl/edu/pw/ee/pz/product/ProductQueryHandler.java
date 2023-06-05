package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.product.SearchProductQuery.SearchProductByBasicCriteriaQuery;
import pl.edu.pw.ee.pz.product.SearchProductQuery.SearchProductByIdQuery;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;

@RequiredArgsConstructor
class ProductQueryHandler implements ProductQueryService {

  private final ProductProjectionPort productProjectionPort;

  public Uni<PageResult<Product>> searchByBasicCriteria(SearchProductQuery query) {
    if (query instanceof SearchProductByIdQuery byIdCommand) {
      return productProjectionPort.findById(byIdCommand.id())
          .onItem().transform(PageResult::single);
    } else if (query instanceof SearchProductByBasicCriteriaQuery byBasicCriteriaQuery) {
      return productProjectionPort.findByBasicCriteria(byBasicCriteriaQuery);
    } else {
      return Uni.createFrom().failure(new IllegalArgumentException(
          "Cannot handle %s query: %s".formatted(query.getClass().getSimpleName(), query.toString())
      ));
    }
  }
}
