package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.sharedkernel.model.Pieces;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;

public class InsufficientProductVariationPiecesException extends RuntimeException {

  private InsufficientProductVariationPiecesException(String message) {
    super(message);
  }

  public static InsufficientProductVariationPiecesException insufficientPiecesForRemoval(
      ProductId product,
      ProductVariationId variation,
      Pieces pieces,
      Pieces removedPieces
  ) {
    throw new InsufficientProductVariationPiecesException(
        "Product %s variation %s available pieces is %d. Tried to remove %d pieces".formatted(
            product.value(),
            variation.value(),
            pieces.value(),
            removedPieces.value()
        )
    );
  }
}
