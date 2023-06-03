package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import java.util.List;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

@RequiredArgsConstructor(access = PACKAGE)
class AddVariationSqlOperation {
  
  private final PgPool client;
  private final InsertVariationsSqlOperation insertVariationsSqlOperation;

  public Uni<Void> execute(
      ProductId product,
      ProductVariation variation
  ) {
    return client.withTransaction(
        sqlConnection -> insertVariationsSqlOperation.execute(sqlConnection, product, List.of(variation))
    );
  }
}
