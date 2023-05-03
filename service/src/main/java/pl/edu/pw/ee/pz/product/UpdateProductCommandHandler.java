package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.product.port.ProductAggregatePort;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler.NoResultCommandHandler;

@RequiredArgsConstructor
public class UpdateProductCommandHandler implements NoResultCommandHandler<UpdateProductCommand> {

  private final ProductAggregatePort productAggregatePort;

  @Override
  public Uni<Void> handle(UpdateProductCommand command) {
    return productAggregatePort.findById(command.id())
        .onItem().transform(product -> {
          product.changeCode(command.code());
          product.changeBrand(command.brand());
          updateVariations(command, product);
          return product;
        })
        .onItem().transformToUni(productAggregatePort::save);
  }

  private void updateVariations(UpdateProductCommand command, ProductAggregate product) {
    product.updateVariations(command.variations());
  }
}
