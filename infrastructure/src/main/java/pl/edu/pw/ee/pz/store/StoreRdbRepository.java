package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

@RequiredArgsConstructor(access = PACKAGE)
class StoreRdbRepository implements StoreProjectionPort {

  private final InsertStoreSqlOperation insertStoreSqlOperation;
  private final FindStoreByIdSqlOperation findStoreByIdSqlOperation;

  @Override
  public Uni<Void> addStore(Store store) {
    return insertStoreSqlOperation.execute(store);
  }

  @Override
  public Uni<Store> findById(StoreId id) {
    return findStoreByIdSqlOperation.execute(id);
  }
}
