package pl.edu.pw.ee.pz.product;

import static java.util.stream.Collectors.groupingBy;
import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeType;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeValue;

@RequiredArgsConstructor(access = PACKAGE)
class FindProductByIdSqlOperation {

  private final PgPool client;

  public Uni<Product> execute(ProductId id) {
    return client.preparedQuery("""
            SELECT
              p.id
              ,p.code
              ,p.brand_id
              ,pva.variation_id
              ,pva.type
              ,pva.value
            FROM 
              products p
              LEFT JOIN product_variation_attributes pva ON
                pva.product_id = p.id
            WHERE 
              p.id = $1
            """)
        .execute(Tuple.of(id.value()))
        .onItem().transformToMulti(RowSet::toMulti)
        .onItem().transform(FindProductByIdSqlOperation::toProductVariationAttributeView)
        .collect().asList()
        .onItem().transform(FindProductByIdSqlOperation::toProduct);
  }

  private static ProductVariationAttributeView toProductVariationAttributeView(Row row) {
    return new ProductVariationAttributeView(
        row.getString("id"),
        row.getString("code"),
        row.getString("brand_id"),
        Objects.requireNonNullElse(row.getString("variation_id"), ""),
        row.getString("type"),
        row.getString("value")
    );
  }

  private static Product toProduct(List<ProductVariationAttributeView> attributesByVariationId) {
    var first = attributesByVariationId.get(0);
    var productId = new ProductId(UUID.fromString(first.productId()));
    var code = new ProductCode(first.productCode());
    var brand = new BrandId(UUID.fromString(first.brandId()));
    Set<ProductVariation> productVariations = toProductVariations(attributesByVariationId);
    return new Product(
        productId,
        code,
        brand,
        productVariations
    );
  }

  private static Set<ProductVariation> toProductVariations(List<ProductVariationAttributeView> productAndVariations) {
    var variations = productAndVariations.stream().collect(groupingBy(ProductVariationAttributeView::variationId));
    return variations.entrySet().stream()
        .map(variation -> new ProductVariation(
            new ProductVariationId(UUID.fromString(variation.getKey())),
            variation.getValue().stream()
                .map(attribute -> new VariationAttribute(
                    new AttributeType(attribute.attributeType()),
                    new AttributeValue(attribute.attributeValue())
                ))
                .collect(Collectors.toUnmodifiableSet())
        ))
        .collect(Collectors.toUnmodifiableSet());
  }

  private record ProductVariationAttributeView(
      String productId,
      String productCode,
      String brandId,
      String variationId,
      String attributeType,
      String attributeValue
  ) {

  }
}
