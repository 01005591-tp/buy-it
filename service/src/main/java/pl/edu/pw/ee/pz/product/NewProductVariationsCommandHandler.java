package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.product.port.ProductPort;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler.NoResultCommandHandler;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation.VariationId;

@RequiredArgsConstructor
public class NewProductVariationsCommandHandler implements NoResultCommandHandler<NewProductVariationsCommand> {

  private final ProductPort productPort;

  @Override
  public Uni<Void> handle(NewProductVariationsCommand command) {
    return productPort.findById(command.product())
        .onItem().transform(product -> addVariations(command, product))
        .onItem().transformToUni(productPort::save);
  }

  private ProductAggregate addVariations(NewProductVariationsCommand command, ProductAggregate product) {
    command.variations().forEach(variation -> product.addVariation(toProductVariation(variation)));
    return product;
  }

  private ProductVariation toProductVariation(NewProductVariation variation) {
    return new ProductVariation(new VariationId(UUID.randomUUID()), variation.attributes());
  }
}
