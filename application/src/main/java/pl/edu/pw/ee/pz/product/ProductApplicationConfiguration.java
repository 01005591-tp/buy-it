package pl.edu.pw.ee.pz.product;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandlerExecutor;

@ApplicationScoped
public class ProductApplicationConfiguration {

  @Produces
  ProductVariationsMapper productVariationsMapper() {
    return new ProductVariationsMapper();
  }

  @Produces
  CreateProductEndpoint createProductEndpoint(
      CommandHandlerExecutor commandHandlerExecutor,
      ProductVariationsMapper productVariationsMapper
  ) {
    return new CreateProductEndpoint(commandHandlerExecutor, productVariationsMapper);
  }

  @Produces
  UpdateProductEndpoint updateProductEndpoint(
      CommandHandlerExecutor commandHandlerExecutor,
      ProductVariationsMapper productVariationsMapper
  ) {
    return new UpdateProductEndpoint(commandHandlerExecutor, productVariationsMapper);
  }
}