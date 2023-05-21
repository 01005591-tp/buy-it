package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PACKAGE)
class InsertProductSqlOperation {

  private final PgPool client;
  private final InsertVariationsSqlOperation insertVariationsSqlOperation;

  public Uni<Void> execute(Product product) {
    return client.withTransaction(sqlConnection ->
        sqlConnection.preparedQuery("""
                  INSERT INTO products (keyset_id, id, code, brand_id) VALUES (nextval('products_seq'), $1, $2, $3)
                  ON CONFLICT (id)
                  DO UPDATE SET code = $2, brand_id = $3
                """)
            .execute(Tuple.of(product.id().value(), product.code().value(), product.brand().value()))
            .replaceWithVoid()
            .onItem().transformToUni(success -> insertVariationsSqlOperation.execute(
                sqlConnection,
                product.id(),
                List.copyOf(product.variations())
            ))
    );
  }
}
