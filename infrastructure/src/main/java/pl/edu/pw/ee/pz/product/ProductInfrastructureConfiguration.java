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
      ChangeBrandSqlOperation changeBrandSqlOperation,
      FindProductsByBasicCriteriaSqlOperation findProductsByBasicCriteriaSqlOperation
  ) {
    return new ProductRdbRepository(
        insertProductSqlOperation,
        findProductByIdSqlOperation,
        addVariationSqlOperation,
        removeVariationSqlOperation,
        replaceVariationsSqlOperation,
        changeCodeSqlOperation,
        changeBrandSqlOperation,
        findProductsByBasicCriteriaSqlOperation
    );
  }

  @Produces
  ProductDbMapper productDbMapper() {
    return new ProductDbMapper();
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
  FindProductByIdSqlOperation findProductByIdSqlOperation(
      PgPool pgPool,
      ProductDbMapper productDbMapper
  ) {
    return new FindProductByIdSqlOperation(pgPool, productDbMapper);
  }

  @Produces
  AddVariationSqlOperation addVariationSqlOperation(
      PgPool pgPool,
      InsertVariationsSqlOperation insertVariationsSqlOperation
  ) {
    return new AddVariationSqlOperation(pgPool, insertVariationsSqlOperation);
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

  @Produces
  FindProductsByBasicCriteriaSqlOperation findProductsByBasicCriteriaSqlOperation(
      PgPool pgPool,
      ProductDbMapper productDbMapper
  ) {
    return new FindProductsByBasicCriteriaSqlOperation(
        pgPool,
        productDbMapper
    );
  }
}
