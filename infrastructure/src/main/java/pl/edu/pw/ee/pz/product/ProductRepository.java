package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.event.EventStoreRepository;
import pl.edu.pw.ee.pz.product.port.ProductPort;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

@RequiredArgsConstructor(access = PACKAGE)
class ProductRepository implements ProductPort {

  private final EventStoreRepository eventStoreRepository;

  @Override
  public Uni<ProductAggregate> findById(ProductId productId) {
    return eventStoreRepository.findById(ProductAggregate.aggregateType(), productId, ProductAggregate::new);
  }

  @Override
  public Uni<Void> save(ProductAggregate product) {
    return eventStoreRepository.save(product);
  }
}
