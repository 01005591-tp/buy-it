package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.List;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

@RequiredArgsConstructor(access = PACKAGE)
class AddVariationSqlOperation {

  private final PgPool client;

  public Uni<Void> execute(
      ProductId product,
      ProductVariation variation
  ) {
    return client.preparedQuery("""
            INSERT INTO product_variation_attributes (variation_id, product_id, type, value) VALUES ($1, $2, $3, $4)
            """)
        .executeBatch(toQueryParams(product, variation))
        .replaceWithVoid();
  }

  private static List<Tuple> toQueryParams(
      ProductId product,
      ProductVariation variation
  ) {
    return variation.attributes().stream()
        .map(attribute -> Tuple.of(
            variation.id().value(),
            product.value(),
            attribute.type().value(),
            attribute.value().value()
        ))
        .toList();
  }
}
