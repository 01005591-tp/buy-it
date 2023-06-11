package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import com.eventstore.dbclient.StreamNotFoundException;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.event.EventStoreRepository;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;
import pl.edu.pw.ee.pz.store.port.StoreAggregatePort;
import pl.edu.pw.ee.pz.store.port.StoreNotFoundException;

@RequiredArgsConstructor(access = PACKAGE)
class StoreAggregateRepository implements StoreAggregatePort {

  private final EventStoreRepository eventStoreRepository;

  @Override
  public Uni<StoreAggregate> findById(StoreId storeId) {
    return eventStoreRepository.findById(StoreAggregate.aggregateType(), storeId, StoreAggregate::new)
        .onFailure(StreamNotFoundException.class)
        .transform(cause -> StoreNotFoundException.notFound(storeId, cause));

  }

  @Override
  public Uni<Void> save(StoreAggregate store) {
    return eventStoreRepository.save(store);
  }
}
