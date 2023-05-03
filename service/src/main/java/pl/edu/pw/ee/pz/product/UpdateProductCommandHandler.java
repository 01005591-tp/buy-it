package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.product.port.ProductPort;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler.NoResultCommandHandler;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation.VariationId;

@RequiredArgsConstructor
public class UpdateProductCommandHandler implements NoResultCommandHandler<UpdateProductCommand> {

  private final ProductPort productPort;

  @Override
  public Uni<Void> handle(UpdateProductCommand command) {
    return productPort.findById(command.id())
        .onItem().transform(product -> {
          product.changeCode(command.code());
          product.changeBrand(command.brand());
          updateVariations(command, product);
          return product;
        })
        .onItem().transformToUni(productPort::save);
  }

  private void updateVariations(UpdateProductCommand command, ProductAggregate product) {
    var variations = command.variations().stream()
        .map(this::toProductVariation)
        .toList();
    product.updateVariations(variations);
  }

  private ProductVariation toProductVariation(NewProductVariation variation) {
    return new ProductVariation(new VariationId(UUID.randomUUID()), variation.attributes());
  }
}
