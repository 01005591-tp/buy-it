package pl.edu.pw.ee.pz.product;

import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.event.EventStoreRepository;

@ApplicationScoped
public class ProductInfrastructureConfiguration {

  @Produces
  ProductAggregateRepository productAggregateRepository(EventStoreRepository eventStoreRepository) {
    return new ProductAggregateRepository(eventStoreRepository);
  }

  @Produces
  ProductRdbRepository productRdbRepository(
      InsertProductSqlOperation insertProductSqlOperation,
      FindProductByIdSqlOperation findProductByIdSqlOperation,
      AddVariationSqlOperation addVariationSqlOperation,
      RemoveVariationSqlOperation removeVariationSqlOperation,
      ReplaceVariationsSqlOperation replaceVariationsSqlOperation,
      ChangeCodeSqlOperation changeCodeSqlOperation,
      ChangeBrandSqlOperation changeBrandSqlOperation
  ) {
    return new ProductRdbRepository(
        insertProductSqlOperation,
        findProductByIdSqlOperation,
        addVariationSqlOperation,
        removeVariationSqlOperation,
        replaceVariationsSqlOperation,
        changeCodeSqlOperation,
        changeBrandSqlOperation
    );
  }

  @Produces
  InsertVariationsSqlOperation insertVariationsSqlOperation() {
    return new InsertVariationsSqlOperation();
  }

  @Produces
  InsertProductSqlOperation insertProductSqlOperation(
      PgPool pgPool,
      InsertVariationsSqlOperation insertVariationsSqlOperation
  ) {
    return new InsertProductSqlOperation(pgPool, insertVariationsSqlOperation);
  }

  @Produces
  FindProductByIdSqlOperation findProductByIdSqlOperation(PgPool pgPool) {
    return new FindProductByIdSqlOperation(pgPool);
  }

  @Produces
  AddVariationSqlOperation addVariationSqlOperation(PgPool pgPool) {
    return new AddVariationSqlOperation(pgPool);
  }

  @Produces
  RemoveVariationSqlOperation removeVariationSqlOperation(PgPool pgPool) {
    return new RemoveVariationSqlOperation(pgPool);
  }

  @Produces
  ReplaceVariationsSqlOperation replaceVariationsSqlOperation(
      PgPool pgPool,
      InsertVariationsSqlOperation insertVariationsSqlOperation
  ) {
    return new ReplaceVariationsSqlOperation(pgPool, insertVariationsSqlOperation);
  }

  @Produces
  ChangeCodeSqlOperation changeCodeSqlOperation(PgPool pgPool) {
    return new ChangeCodeSqlOperation(pgPool);
  }

  @Produces
  ChangeBrandSqlOperation changeBrandSqlOperation(PgPool pgPool) {
    return new ChangeBrandSqlOperation(pgPool);
  }
}
