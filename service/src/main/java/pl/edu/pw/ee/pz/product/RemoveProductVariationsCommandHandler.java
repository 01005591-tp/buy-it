package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.product.port.ProductPort;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler.NoResultCommandHandler;

@RequiredArgsConstructor
public class RemoveProductVariationsCommandHandler implements NoResultCommandHandler<RemoveProductVariationsCommand> {

  private final ProductPort productPort;

  @Override
  public Uni<Void> handle(RemoveProductVariationsCommand command) {
    return productPort.findById(command.product())
        .onItem().transform(product -> removeVariations(command, product))
        .onItem().transformToUni(productPort::save);
  }

  private ProductAggregate removeVariations(RemoveProductVariationsCommand command, ProductAggregate product) {
    command.variations().forEach(product::removeVariation);
    return product;
  }
}
