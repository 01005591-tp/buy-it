package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.product.port.ProductAggregatePort;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler.NoResultCommandHandler;

@RequiredArgsConstructor(access = PACKAGE)
class RemoveProductVariationsCommandHandler implements NoResultCommandHandler<RemoveProductVariationsCommand> {

  private final ProductAggregatePort productAggregatePort;

  @Override
  public Uni<Void> handle(RemoveProductVariationsCommand command) {
    return productAggregatePort.findById(command.product())
        .onItem().transform(product -> removeVariations(command, product))
        .onItem().transformToUni(productAggregatePort::save);
  }

  private ProductAggregate removeVariations(RemoveProductVariationsCommand command, ProductAggregate product) {
    command.variations().forEach(product::removeVariation);
    return product;
  }
}
