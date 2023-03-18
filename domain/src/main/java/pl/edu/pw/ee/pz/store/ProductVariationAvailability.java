package pl.edu.pw.ee.pz.store;

import lombok.AccessLevel;
import lombok.With;

public record ProductVariationAvailability(
    ProductVariation variation,
    @With(AccessLevel.PRIVATE)
    ProductVariationPieces pieces
) {

  public static ProductVariationAvailability empty(ProductVariation variation) {
    return new ProductVariationAvailability(variation, ProductVariationPieces.none());
  }

  public ProductVariationAvailability addPieces(ProductVariationPieces other) {
    return withPieces(this.pieces.add(other));
  }

  public ProductVariationAvailability removePieces(ProductVariationPieces other) {
    return withPieces(this.pieces.remove(other));
  }
}
