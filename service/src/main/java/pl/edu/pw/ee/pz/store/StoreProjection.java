package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.Projection;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesUpdated;
import pl.edu.pw.ee.pz.store.event.StoreCreated;
import pl.edu.pw.ee.pz.store.port.StoreAggregatePort;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class StoreProjection implements Projection {

  private final StoreProjectionPort storeProjectionPort;
  private final StoreAggregatePort storeAggregatePort;

  @Override
  public List<Class<? extends DomainEvent<?>>> supportedEvents() {
    return List.of(
        StoreCreated.class,
        ProductVariationPiecesUpdated.class
    );
  }

  @Override
  public Uni<Void> handle(DomainEvent<?> event) {
    if (event instanceof StoreCreated storeCreated) {
      return handle(storeCreated)
          .onFailure().call(throwable -> recover(storeCreated, throwable));
    } else if (event instanceof ProductVariationPiecesUpdated productVariationPiecesUpdated) {
      return handle(productVariationPiecesUpdated)
          .onFailure().call(throwable -> recover(productVariationPiecesUpdated, throwable));
    }
    log.debug("Ignored unsupported event {}", event.getClass().getSimpleName());
    return Uni.createFrom().voidItem();
  }

  private Uni<Void> handle(StoreCreated event) {
    var store = new Store(
        event.header().aggregateId(),
        event.code(),
        event.address(),
        Map.of()
    );
    return storeProjectionPort.addStore(store)
        .onItem().invoke(() -> log.info("Store stored {}", store.id().value()));
  }

  private Uni<Void> handle(ProductVariationPiecesUpdated event) {
    log.error("Projection event handling not yet implemented for event ProductVariationPiecesUpdated {}", event);
    return Uni.createFrom().voidItem();
  }

  private Uni<Void> recover(DomainEvent<StoreId> event, Throwable throwable) {
    log.warn("Could not process event {}", event, throwable);
    var productId = event.header().aggregateId();
    return storeAggregatePort.findById(productId)
        .onItem().transform(storeAggregate -> new Store(
            storeAggregate.id(),
            storeAggregate.code(),
            storeAggregate.address(),
            storeAggregate.products()
        ))
        .onItem().transformToUni(storeProjectionPort::addStore)
        .onItem().invoke(() -> log.info("Recovered from error during event processing {}", event));
  }
}
