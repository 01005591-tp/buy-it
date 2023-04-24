package pl.edu.pw.ee.pz.brand;

import io.smallrye.mutiny.Uni;
import pl.edu.pw.ee.pz.sharedkernel.query.PageQuery;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;
import pl.edu.pw.ee.pz.sharedkernel.query.Query;
import pl.edu.pw.ee.pz.sharedkernel.query.RequestedPage;

public interface BrandQueryService {

  Uni<Brand> handle(GetBrandByIdQuery query);

  Uni<PageResult<Brand>> handle(FindBrandsByCriteriaQuery query);


  record GetBrandByIdQuery(String id) implements Query {

  }

  record FindBrandsByCriteriaQuery(String code, RequestedPage page) implements PageQuery {

  }
}
