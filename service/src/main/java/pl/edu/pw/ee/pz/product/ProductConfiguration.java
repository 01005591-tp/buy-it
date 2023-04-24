package pl.edu.pw.ee.pz.product;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.product.port.ProductPort;

@ApplicationScoped
public class ProductConfiguration {

  @Produces
  NewProductCommandHandler newProductCommandHandler(ProductPort productPort) {
    return new NewProductCommandHandler(productPort);
  }

  @Produces
  NewProductVariationsCommandHandler newProductVariationsCommandHandler(ProductPort productPort) {
    return new NewProductVariationsCommandHandler(productPort);
  }

  @Produces
  RemoveProductVariationsCommandHandler removeProductVariationsCommandHandler(ProductPort productPort) {
    return new RemoveProductVariationsCommandHandler(productPort);
  }

  @Produces
  UpdateProductCommandHandler updateProductCommandHandler(ProductPort productPort) {
    return new UpdateProductCommandHandler(productPort);
  }
}
