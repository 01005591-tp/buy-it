package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import com.eventstore.dbclient.StreamNotFoundException;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.event.EventStoreRepository;
import pl.edu.pw.ee.pz.product.port.ProductAggregatePort;
import pl.edu.pw.ee.pz.product.port.ProductNotFoundException;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

@RequiredArgsConstructor(access = PACKAGE)
class ProductAggregateRepository implements ProductAggregatePort {

  private final EventStoreRepository eventStoreRepository;

  @Override
  public Uni<ProductAggregate> findById(ProductId productId) {
    return eventStoreRepository.findById(ProductAggregate.aggregateType(), productId, ProductAggregate::new)
        .onFailure(StreamNotFoundException.class)
        .transform(cause -> ProductNotFoundException.notFound(productId, cause));
  }

  @Override
  public Uni<Void> save(ProductAggregate product) {
    return eventStoreRepository.save(product);
  }
}
