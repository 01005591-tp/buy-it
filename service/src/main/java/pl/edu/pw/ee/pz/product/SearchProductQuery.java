package pl.edu.pw.ee.pz.product;

import io.vavr.control.Option;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.query.PageQuery;
import pl.edu.pw.ee.pz.sharedkernel.query.RequestedPage;

public interface SearchProductQuery extends PageQuery {

  static SearchProductByIdQuery byId(ProductId productId) {
    return new SearchProductByIdQuery(productId);
  }

  static SearchProductByBasicCriteriaQuery byBasicCriteria(
      Option<ProductCode> code,
      Option<BrandId> brand,
      RequestedPage page
  ) {
    return new SearchProductByBasicCriteriaQuery(code, brand, page);
  }

  record SearchProductByIdQuery(
      ProductId id
  ) implements SearchProductQuery {

    @Override
    public RequestedPage page() {
      return RequestedPage.single();
    }
  }

  record SearchProductByBasicCriteriaQuery(
      Option<ProductCode> code,
      Option<BrandId> brand,
      RequestedPage page
  ) implements SearchProductQuery {

  }
}
