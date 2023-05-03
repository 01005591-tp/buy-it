package pl.edu.pw.ee.pz.brand;

import static lombok.AccessLevel.PACKAGE;

import com.eventstore.dbclient.StreamNotFoundException;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.ee.pz.brand.port.BrandAggregatePort;
import pl.edu.pw.ee.pz.brand.port.BrandNotFoundException;
import pl.edu.pw.ee.pz.event.EventStoreRepository;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class BrandAggregateRepository implements BrandAggregatePort {

  private final EventStoreRepository eventStoreRepository;

  @Override
  public Uni<BrandAggregate> findById(BrandId brandId) {
    return eventStoreRepository.findById(BrandAggregate.aggregateType(), brandId, BrandAggregate::new)
        .onFailure(StreamNotFoundException.class).transform(cause -> BrandNotFoundException.notFound(brandId, cause));
  }

  @Override
  public Uni<Void> save(BrandAggregate brand) {
    return eventStoreRepository.save(brand);
  }
}
