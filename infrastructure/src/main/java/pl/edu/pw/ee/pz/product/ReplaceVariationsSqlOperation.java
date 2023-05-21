package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.List;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

@RequiredArgsConstructor(access = PACKAGE)
class ReplaceVariationsSqlOperation {

  private final PgPool client;
  private final InsertVariationsSqlOperation insertVariationsSqlOperation;

  public Uni<Void> execute(
      ProductId product,
      List<ProductVariation> variations
  ) {
    return client.withTransaction(sqlConnection ->
        deleteVariations(sqlConnection, product)
            .onItem().transformToUni(
                success -> insertVariationsSqlOperation.execute(
                    sqlConnection,
                    product,
                    variations
                ))
    );
  }

  private Uni<Void> deleteVariations(SqlConnection sqlConnection, ProductId product) {
    return sqlConnection.preparedQuery("""
            DELETE FROM product_variation_attributes WHERE product_id = $1
            """)
        .execute(Tuple.of(product.value()))
        .replaceWithVoid();
  }
}
