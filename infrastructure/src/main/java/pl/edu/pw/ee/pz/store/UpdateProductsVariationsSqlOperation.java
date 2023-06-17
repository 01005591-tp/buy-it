package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationPieces.VariationPieces;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

@RequiredArgsConstructor(access = PACKAGE)
class UpdateProductsVariationsSqlOperation {

  private final PgPool client;
  private final InsertVariationPiecesSqlOperation insertVariationPiecesSqlOperation;

  public Uni<Void> execute(
      StoreId storeId,
      ProductId productId,
      List<VariationPieces> variationPieces
  ) {
    var variationPiecesForUpdate = variationPieces.stream().collect(Collectors.toMap(
        VariationPieces::variation,
        VariationPieces::pieces
    ));
    var productVariationPieces = Map.of(productId, variationPiecesForUpdate);
    return client.withTransaction(sqlConnection -> insertVariationPiecesSqlOperation.execute(
        sqlConnection,
        storeId,
        productVariationPieces
    ));
  }
}
