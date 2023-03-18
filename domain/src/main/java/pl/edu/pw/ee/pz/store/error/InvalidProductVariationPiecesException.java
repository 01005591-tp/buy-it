package pl.edu.pw.ee.pz.store.error;

public class InvalidProductVariationPiecesException extends RuntimeException {

  private InvalidProductVariationPiecesException(String message) {
    super(message);
  }

  public static InvalidProductVariationPiecesException negativeCount(Long count) {
    return new InvalidProductVariationPiecesException(
        "Pieces count cannot be negative. Requested count was %d".formatted(count)
    );
  }

}
