package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.product.port.ProductAggregatePort;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

@RequiredArgsConstructor
public class NewProductCommandHandler implements CommandHandler<NewProductCommand, ProductId> {

  private final ProductAggregatePort productAggregatePort;

  @Override
  public Uni<ProductId> handle(NewProductCommand command) {
    var product = new ProductAggregate(
        new ProductId(UUID.randomUUID()),
        command.code(),
        command.brand()
    );
    command.variations().forEach(variation -> product.addVariation(new ProductVariation(
        variation.attributes()
    )));
    return productAggregatePort.save(product)
        .onItem().transform(success -> product.id());
  }
}
