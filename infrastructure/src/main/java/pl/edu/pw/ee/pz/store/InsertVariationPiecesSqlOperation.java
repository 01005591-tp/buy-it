package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;
import static pl.edu.pw.ee.pz.sharedkernel.function.MapUtil.mapEntry;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.Pieces;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

@RequiredArgsConstructor(access = PACKAGE)
class InsertVariationPiecesSqlOperation {

  Uni<Void> execute(
      SqlConnection sqlConnection,
      StoreId storeId,
      Map<ProductId, Map<ProductVariationId, Pieces>> products
  ) {
    if (products.isEmpty()) {
      return Uni.createFrom().voidItem();
    }
    return sqlConnection.preparedQuery("""
            INSERT INTO store_product_pieces (store_id, product_id, variation_id, pieces) VALUES ($1, $2, $3, $4)
            ON CONFLICT (store_id, product_id, variation_id)
            DO UPDATE SET pieces = $4
            """)
        .executeBatch(toSqlParams(storeId, products))
        .replaceWithVoid();
  }

  private static List<Tuple> toSqlParams(
      StoreId storeId,
      Map<ProductId, Map<ProductVariationId, Pieces>> products
  ) {
    return products.entrySet().stream()
        .flatMap(mapEntry((product, variations) -> variations.entrySet().stream()
            .map(mapEntry((variation, pieces) -> Tuple.of(
                storeId.value(),
                product.value(),
                variation.value(),
                pieces.value()
            )))
        ))
        .toList();
  }
}
