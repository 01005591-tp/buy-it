package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import java.util.List;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.product.SearchProductQuery.SearchProductByBasicCriteriaQuery;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;

@RequiredArgsConstructor(access = PACKAGE)
class ProductRdbRepository implements ProductProjectionPort {

  private final InsertProductSqlOperation addProductSqlOperation;
  private final FindProductByIdSqlOperation findProductByIdSqlOperation;
  private final AddVariationSqlOperation addVariationSqlOperation;
  private final RemoveVariationSqlOperation removeVariationSqlOperation;
  private final ReplaceVariationsSqlOperation replaceVariationsSqlOperation;
  private final ChangeCodeSqlOperation changeCodeSqlOperation;
  private final ChangeBrandSqlOperation changeBrandSqlOperation;
  private final FindProductsByBasicCriteriaSqlOperation findProductsByBasicCriteriaSqlOperation;

  @Override
  public Uni<Void> addProduct(Product product) {
    return addProductSqlOperation.execute(product);
  }

  @Override
  public Uni<Void> addVariation(ProductId product, ProductVariation variation) {
    return addVariationSqlOperation.execute(product, variation);
  }

  @Override
  public Uni<Void> removeVariation(ProductId product, ProductVariationId variation) {
    return removeVariationSqlOperation.execute(product, variation);
  }

  @Override
  public Uni<Void> replaceVariations(ProductId product, List<ProductVariation> variations) {
    return replaceVariationsSqlOperation.execute(product, variations);
  }

  @Override
  public Uni<Void> changeCode(ProductId product, ProductCode code) {
    return changeCodeSqlOperation.execute(product, code);
  }

  @Override
  public Uni<Void> changeBrand(ProductId product, BrandId brand) {
    return changeBrandSqlOperation.execute(product, brand);
  }

  @Override
  public Uni<Product> findById(ProductId id) {
    return findProductByIdSqlOperation.execute(id);
  }

  @Override
  public Uni<PageResult<Product>> findByBasicCriteria(SearchProductByBasicCriteriaQuery query) {
    return findProductsByBasicCriteriaSqlOperation.execute(query);
  }
}
