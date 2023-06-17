package pl.edu.pw.ee.pz.store;

import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.event.EventStoreRepository;
import pl.edu.pw.ee.pz.store.port.StoreAggregatePort;

@ApplicationScoped
public class StoreInfrastructureConfiguration {

  @Produces
  StoreAggregatePort storeAggregatePort(EventStoreRepository eventStoreRepository) {
    return new StoreAggregateRepository(eventStoreRepository);
  }

  @Produces
  StoreProjectionPort storeProjectionPort(
      InsertStoreSqlOperation insertStoreSqlOperation,
      FindStoreByIdSqlOperation findStoreByIdSqlOperation,
      UpdateProductsVariationsSqlOperation updateProductsVariationsSqlOperation
  ) {
    return new StoreRdbRepository(
        insertStoreSqlOperation,
        findStoreByIdSqlOperation,
        updateProductsVariationsSqlOperation
    );
  }

  @Produces
  FindStoreByIdSqlOperation findStoreByIdSqlOperation(PgPool pgPool) {
    return new FindStoreByIdSqlOperation(pgPool);
  }

  @Produces
  InsertVariationPiecesSqlOperation insertVariationPiecesSqlOperation() {
    return new InsertVariationPiecesSqlOperation();
  }

  @Produces
  InsertStoreSqlOperation insertStoreSqlOperation(
      PgPool pgPool,
      InsertVariationPiecesSqlOperation insertVariationPiecesSqlOperation
  ) {
    return new InsertStoreSqlOperation(pgPool, insertVariationPiecesSqlOperation);
  }

  @Produces
  UpdateProductsVariationsSqlOperation updateProductsVariationsSqlOperation(
      PgPool pgPool,
      InsertVariationPiecesSqlOperation insertVariationPiecesSqlOperation
  ) {
    return new UpdateProductsVariationsSqlOperation(pgPool, insertVariationPiecesSqlOperation);
  }
}
