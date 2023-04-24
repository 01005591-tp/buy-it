package pl.edu.pw.ee.pz.brand.port;

import io.smallrye.mutiny.Uni;
import pl.edu.pw.ee.pz.brand.BrandAggregate;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;

public interface BrandAggregatePort {

  Uni<BrandAggregate> findById(BrandId brandId);

  Uni<Void> save(BrandAggregate brand);
}
