package pl.edu.pw.ee.pz.brand;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.brand.BrandProjectionPort.SearchCriteria;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;

@RequiredArgsConstructor(access = PACKAGE)
class BrandQueryHandler implements BrandQueryService {

  private final BrandProjectionPort brandProjectionPort;

  @Override
  public Uni<Brand> handle(GetBrandByIdQuery query) {
    var uuid = UUID.fromString(query.id());
    var id = new BrandId(uuid);
    return brandProjectionPort.findById(id);
  }

  @Override
  public Uni<PageResult<Brand>> handle(FindBrandsByCriteriaQuery query) {
    return brandProjectionPort.findByCriteria(new SearchCriteria(query.code(), query.page()));
  }
}
