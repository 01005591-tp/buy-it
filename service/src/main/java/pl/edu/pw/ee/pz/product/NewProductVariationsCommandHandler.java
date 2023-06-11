package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.product.port.ProductAggregatePort;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler.NoResultCommandHandler;

@RequiredArgsConstructor(access = PACKAGE)
class NewProductVariationsCommandHandler implements NoResultCommandHandler<NewProductVariationsCommand> {

  private final ProductAggregatePort productAggregatePort;

  @Override
  public Uni<Void> handle(NewProductVariationsCommand command) {
    return productAggregatePort.findById(command.product())
        .onItem().transform(product -> addVariations(command, product))
        .onItem().transformToUni(productAggregatePort::save);
  }

  private ProductAggregate addVariations(NewProductVariationsCommand command, ProductAggregate product) {
    command.variations().forEach(product::addVariation);
    return product;
  }
}
