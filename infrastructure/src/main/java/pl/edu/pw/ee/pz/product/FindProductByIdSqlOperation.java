package pl.edu.pw.ee.pz.product;

import static java.util.stream.Collectors.groupingBy;
import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import java.math.BigDecimal;
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

  private static final ProductVariationId EMPTY_PRODUCT_VARIATION_ID = new ProductVariationId(null);

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
              ,pva.value_type
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
        row.getString("value"),
        ProductVariationAttributeValueType.tryParse(row.getString("value_type"))
            .orElse(ProductVariationAttributeValueType.STRING)
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
            toProductVariationId(variation.getKey()),
            variation.getValue().stream()
                .map(attribute -> new VariationAttribute<>(
                    new AttributeType(attribute.attributeType()),
                    toAttributeValue(attribute)
                ))
                .collect(Collectors.toUnmodifiableSet())
        ))
        .collect(Collectors.toUnmodifiableSet());
  }

  private static ProductVariationId toProductVariationId(String variationId) {
    return variationId.isBlank()
        ? EMPTY_PRODUCT_VARIATION_ID
        : new ProductVariationId(UUID.fromString(variationId));
  }

  private static AttributeValue<?> toAttributeValue(ProductVariationAttributeView attribute) {
    return switch (attribute.attributeValueType()) {
      case STRING -> AttributeValue.stringAttribute(attribute.attributeValue());
      case LONG -> AttributeValue.longAttribute(Long.parseLong(attribute.attributeValue()));
      case INTEGER -> AttributeValue.integerAttribute(Integer.parseInt(attribute.attributeValue()));
      case BIG_DECIMAL -> AttributeValue.bigDecimalAttribute(new BigDecimal(attribute.attributeValue()));
    };
  }

  private record ProductVariationAttributeView(
      String productId,
      String productCode,
      String brandId,
      String variationId,
      String attributeType,
      String attributeValue,
      ProductVariationAttributeValueType attributeValueType
  ) {

  }
}
