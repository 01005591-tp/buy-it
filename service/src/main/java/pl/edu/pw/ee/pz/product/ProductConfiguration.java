package pl.edu.pw.ee.pz.product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.product.port.ProductAggregatePort;

@ApplicationScoped
public class ProductConfiguration {

  @Produces
  NewProductCommandHandler newProductCommandHandler(ProductAggregatePort productAggregatePort) {
    return new NewProductCommandHandler(productAggregatePort);
  }

  @Produces
  NewProductVariationsCommandHandler newProductVariationsCommandHandler(ProductAggregatePort productAggregatePort) {
    return new NewProductVariationsCommandHandler(productAggregatePort);
  }

  @Produces
  RemoveProductVariationsCommandHandler removeProductVariationsCommandHandler(
      ProductAggregatePort productAggregatePort
  ) {
    return new RemoveProductVariationsCommandHandler(productAggregatePort);
  }

  @Produces
  UpdateProductCommandHandler updateProductCommandHandler(ProductAggregatePort productAggregatePort) {
    return new UpdateProductCommandHandler(productAggregatePort);
  }
}
