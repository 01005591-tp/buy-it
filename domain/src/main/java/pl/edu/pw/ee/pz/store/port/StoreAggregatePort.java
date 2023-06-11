package pl.edu.pw.ee.pz.store.port;

import io.smallrye.mutiny.Uni;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;
import pl.edu.pw.ee.pz.store.StoreAggregate;

public interface StoreAggregatePort {

  Uni<StoreAggregate> findById(StoreId storeId);

  Uni<Void> save(StoreAggregate store);
}
