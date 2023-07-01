package pl.edu.pw.ee.pz.store;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandlerExecutor;

@ApplicationScoped
public class StoreApplicationConfiguration {

  @Produces
  UpdateProductsAvailablePiecesEndpoint updateProductsAvailabilityEndpoint(
      CommandHandlerExecutor commandHandlerExecutor
  ) {
    return new UpdateProductsAvailablePiecesEndpoint(commandHandlerExecutor);
  }

  @Produces
  CreateStoreEndpoint createStoreEndpoint(
      CommandHandlerExecutor commandHandlerExecutor
  ) {
    return new CreateStoreEndpoint(commandHandlerExecutor);
  }
}
