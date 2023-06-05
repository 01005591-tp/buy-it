package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;

public interface ProductQueryService {

  Uni<PageResult<Product>> searchByBasicCriteria(SearchProductQuery command);
}
