package pl.edu.pw.ee.pz.sharedkernel.model;

import java.util.List;

public record ProductVariationPieces(
    ProductId product,
    List<VariationPieces> variationPieces
) {

  public record VariationPieces(
      ProductVariationId variation,
      Pieces pieces
  ) {

  }
}
