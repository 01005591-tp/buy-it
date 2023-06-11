package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler.NoResultCommandHandler;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationPieces;
import pl.edu.pw.ee.pz.store.port.StoreAggregatePort;

@RequiredArgsConstructor(access = PACKAGE)
class UpdateProductsAvailabilityCommandHandler implements
    NoResultCommandHandler<UpdateProductsAvailabilityCommand> {

  private final StoreAggregatePort storeAggregatePort;

  @Override
  public Uni<Void> handle(UpdateProductsAvailabilityCommand command) {
    return storeAggregatePort.findById(command.store())
        .onItem().invoke(store ->
            setProductVariationPieces(command, store)
        )
        .onItem().transformToUni(storeAggregatePort::save);
  }

  private void setProductVariationPieces(UpdateProductsAvailabilityCommand command, StoreAggregate store) {
    command.productsPieces().forEach(productPieces ->
        store.setProductVariationPieces(toProductVariationPieces(productPieces))
    );
  }

  private ProductVariationPieces toProductVariationPieces(ProductVariationPieces productPieces) {
    return new ProductVariationPieces(
        productPieces.product(),
        productPieces.variationPieces()
    );
  }
}
