package pl.edu.pw.ee.pz.store.error;

import pl.edu.pw.ee.pz.store.ProductVariationPieces;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesRemoved;

public class InsufficientProductVariationPiecesException extends RuntimeException {

  private InsufficientProductVariationPiecesException(String message) {
    super(message);
  }

  public static InsufficientProductVariationPiecesException insufficientPiecesForRemoval(
      ProductVariationPiecesRemoved productVariationPiecesRemoved,
      ProductVariationPieces pieces
  ) {
    throw new InsufficientProductVariationPiecesException(
        "Product %s variation %s available pieces is %d. Tried to remove %d pieces".formatted(
            productVariationPiecesRemoved.product().value().toString(),
            productVariationPiecesRemoved.variation().value().toString(),
            pieces.count(),
            productVariationPiecesRemoved.pieces().count()
        )
    );
  }
}
