package pl.edu.pw.ee.pz.brand;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.brand.port.BrandAggregatePort;
import pl.edu.pw.ee.pz.event.EventStoreRepository;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;

@RequiredArgsConstructor(access = PACKAGE)
class BrandAggregateRepository implements BrandAggregatePort {

  private final EventStoreRepository eventStoreRepository;

  @Override
  public Uni<BrandAggregate> findById(BrandId brandId) {
    return eventStoreRepository.findById(BrandAggregate.aggregateType(), brandId, BrandAggregate::new);
  }

  @Override
  public Uni<Void> save(BrandAggregate brand) {
    return eventStoreRepository.save(brand);
  }
}
