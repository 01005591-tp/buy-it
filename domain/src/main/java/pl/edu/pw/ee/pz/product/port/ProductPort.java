package pl.edu.pw.ee.pz.product.port;

import io.smallrye.mutiny.Uni;
import pl.edu.pw.ee.pz.product.ProductAggregate;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

public interface ProductPort {

  Uni<ProductAggregate> findById(ProductId productId);

  Uni<Void> save(ProductAggregate product);
}
