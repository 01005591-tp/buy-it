package pl.edu.pw.ee.pz.store;

import io.smallrye.mutiny.Uni;
import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationPieces.VariationPieces;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

public interface StoreProjectionPort {

  Uni<Void> addStore(Store store);

  Uni<Void> updateProductsVariations(
      StoreId storeId,
      ProductId productId,
      List<VariationPieces> variationPieces
  );

  Uni<Store> findById(StoreId id);
}
