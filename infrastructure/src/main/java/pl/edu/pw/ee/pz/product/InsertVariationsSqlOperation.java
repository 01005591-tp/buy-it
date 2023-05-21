package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

@Slf4j
class InsertVariationsSqlOperation {

  public Uni<Void> execute(
      SqlConnection sqlConnection,
      ProductId product,
      List<ProductVariation> variations
  ) {
    if (variations.isEmpty()) {
      return Uni.createFrom().voidItem();
    }
    return sqlConnection.preparedQuery("""
            INSERT INTO product_variation_attributes (variation_id, product_id, type, value) VALUES ($1, $2, $3, $4)
            """)
        .executeBatch(toQueryParams(product, variations))
        .replaceWithVoid();
  }

  private static List<Tuple> toQueryParams(ProductId product, List<ProductVariation> variations) {
    return variations.stream()
        .flatMap(variation ->
            variation.attributes().stream()
                .map(attribute -> Tuple.of(
                    variation.id().value(),
                    product.value(),
                    attribute.type().value(),
                    attribute.value().value()
                ))
        )
        .toList();
  }
}
