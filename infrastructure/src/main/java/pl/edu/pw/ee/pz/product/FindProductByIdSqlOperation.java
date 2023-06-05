package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

@RequiredArgsConstructor(access = PACKAGE)
class FindProductByIdSqlOperation {

  private final PgPool client;
  private final ProductDbMapper productDbMapper;

  public Uni<Product> execute(ProductId id) {
    return client.preparedQuery("""
            SELECT
              p.id
              ,p.code
              ,p.brand_id
              ,pva.variation_id
              ,pva.type
              ,pva.value
              ,pva.value_type
            FROM
              products p
              LEFT JOIN product_variation_attributes pva ON
                pva.product_id = p.id
            WHERE
              p.id = $1
            """)
        .execute(Tuple.of(id.value()))
        .onItem().transformToMulti(RowSet::toMulti)
        .onItem().transform(ProductVariationAttributeView::ofRow)
        .collect().asList()
        .onItem().transform(productDbMapper::toProduct);
  }


}
