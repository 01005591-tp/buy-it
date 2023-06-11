package pl.edu.pw.ee.pz.store;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.store.port.StoreAggregatePort;

@ApplicationScoped
public class StoreConfiguration {

  @Produces
  StoreProjection storeProjection(StoreProjectionPort storeProjectionPort, StoreAggregatePort storeAggregatePort) {
    return new StoreProjection(storeProjectionPort, storeAggregatePort);
  }

  @Produces
  UpdateProductsAvailabilityCommandHandler updateProductsAvailabilityCommandHandler(
      StoreAggregatePort storeAggregatePort
  ) {
    return new UpdateProductsAvailabilityCommandHandler(storeAggregatePort);
  }
}
