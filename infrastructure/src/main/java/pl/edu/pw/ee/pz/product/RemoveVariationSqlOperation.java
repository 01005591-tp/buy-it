package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;

@RequiredArgsConstructor(access = PACKAGE)
class RemoveVariationSqlOperation {

  private final PgPool client;

  public Uni<Void> execute(
      ProductId product,
      ProductVariationId variationId
  ) {
    return client.preparedQuery("""
            DELETE FROM product_variation_attributes WHERE product_id = $1 AND variation_id = $2
            """)
        .execute(Tuple.of(product.value(), variationId.value()))
        .replaceWithVoid();
  }
}
