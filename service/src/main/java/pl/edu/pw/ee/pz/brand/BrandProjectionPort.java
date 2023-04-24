package pl.edu.pw.ee.pz.brand;

import io.smallrye.mutiny.Uni;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;
import pl.edu.pw.ee.pz.sharedkernel.query.RequestedPage;

public interface BrandProjectionPort {

  Uni<Void> save(Brand brand);

  Uni<Brand> findById(BrandId id);

  Uni<PageResult<Brand>> findByCriteria(SearchCriteria criteria);

  record SearchCriteria(
      String code,
      RequestedPage requestedPage
  ) {

  }
}
