package pl.edu.pw.ee.pz.store;

import java.util.List;

record UpdateProductAvailabilityRequest(
    List<UpdateProduct> products
) {

  record UpdateProduct(
      String productId,
      List<VariationPieces> variationPieces
  ) {

  }

  record VariationPieces(
      String variationId,
      Long pieces
  ) {

  }

}
