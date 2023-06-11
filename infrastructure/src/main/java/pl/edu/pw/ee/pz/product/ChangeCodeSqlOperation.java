package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

@RequiredArgsConstructor(access = PACKAGE)
class ChangeCodeSqlOperation {

  private final PgPool client;

  Uni<Void> execute(
      ProductId product,
      ProductCode code
  ) {
    return client.preparedQuery("""
            UPDATE products SET code = $2 WHERE id = $1
            """)
        .execute(Tuple.of(product.value(), code.value()))
        .replaceWithVoid();
  }
}
