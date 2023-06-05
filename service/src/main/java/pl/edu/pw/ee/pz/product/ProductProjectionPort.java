package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import java.util.List;
import pl.edu.pw.ee.pz.product.SearchProductQuery.SearchProductByBasicCriteriaQuery;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;

public interface ProductProjectionPort {

  Uni<Void> addProduct(Product product);

  Uni<Void> addVariation(ProductId product, ProductVariation variation);

  Uni<Void> removeVariation(ProductId product, ProductVariationId variation);

  Uni<Void> replaceVariations(ProductId product, List<ProductVariation> variations);

  Uni<Void> changeCode(ProductId product, ProductCode code);

  Uni<Void> changeBrand(ProductId product, BrandId brand);

  Uni<Product> findById(ProductId id);

  Uni<PageResult<Product>> findByBasicCriteria(SearchProductByBasicCriteriaQuery query);
}
