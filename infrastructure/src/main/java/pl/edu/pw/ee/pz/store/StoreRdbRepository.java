package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import java.util.List;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationPieces.VariationPieces;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

@RequiredArgsConstructor(access = PACKAGE)
class StoreRdbRepository implements StoreProjectionPort {

  private final InsertStoreSqlOperation insertStoreSqlOperation;
  private final FindStoreByIdSqlOperation findStoreByIdSqlOperation;
  private final UpdateProductsVariationsSqlOperation updateProductsVariationsSqlOperation;

  @Override
  public Uni<Void> addStore(Store store) {
    return insertStoreSqlOperation.execute(store);
  }

  @Override
  public Uni<Void> updateProductsVariations(
      StoreId storeId,
      ProductId productId,
      List<VariationPieces> variationPieces
  ) {
    return updateProductsVariationsSqlOperation.execute(storeId, productId, variationPieces);
  }

  @Override
  public Uni<Store> findById(StoreId id) {
    return findStoreByIdSqlOperation.execute(id);
  }
}
