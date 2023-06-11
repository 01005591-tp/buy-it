package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandlerExecutor;
import pl.edu.pw.ee.pz.sharedkernel.model.Pieces;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationPieces;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationPieces.VariationPieces;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

@RequiredArgsConstructor(access = PACKAGE)
class UpdateProductsAvailablePiecesEndpoint {

  private final CommandHandlerExecutor commandHandlerExecutor;

  Uni<RestResponse<?>> handle(String id, UpdateProductsAvailabilityRequest request) {
    var command = toUpdateProductsAvailabilityCommand(id, request);
    return commandHandlerExecutor.execute(command)
        .onItem().transform(success -> RestResponse.ok());
  }

  private UpdateProductsAvailabilityCommand toUpdateProductsAvailabilityCommand(
      String id,
      UpdateProductsAvailabilityRequest request
  ) {
    var storeId = new StoreId(UUID.fromString(id));
    var productVariationPieces = request.products().stream()
        .map(this::toProductVariationPieces)
        .toList();
    return new UpdateProductsAvailabilityCommand(storeId, productVariationPieces);
  }

  private ProductVariationPieces toProductVariationPieces(UpdateProductsAvailabilityRequest.UpdateProduct product) {
    var productId = new ProductId(UUID.fromString(product.productId()));
    var variationPieces = product.variationPieces().stream()
        .map(this::toVariationPieces)
        .toList();
    return new ProductVariationPieces(productId, variationPieces);
  }

  private VariationPieces toVariationPieces(UpdateProductsAvailabilityRequest.VariationPieces variationPieces) {
    return new VariationPieces(
        new ProductVariationId(UUID.fromString(variationPieces.variationId())),
        Pieces.of(variationPieces.pieces())
    );
  }
}
