package pl.edu.pw.ee.pz.store;

import io.smallrye.mutiny.Uni;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

public interface StoreProjectionPort {

  Uni<Void> addStore(Store store);

  Uni<Store> findById(StoreId id);
}
