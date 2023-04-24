package pl.edu.pw.ee.pz.product;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.event.EventStoreRepository;

@ApplicationScoped
public class ProductInfrastructureConfiguration {

  @Produces
  ProductRepository productRepository(EventStoreRepository eventStoreRepository) {
    return new ProductRepository(eventStoreRepository);
  }
}
