package pl.edu.pw.ee.pz.product;

import static java.util.Objects.isNull;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeValue;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.BigDecimalAttributeValue;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.IntegerAttributeValue;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.LongAttributeValue;

@Slf4j
class InsertVariationsSqlOperation {

  Uni<Void> execute(
      SqlConnection sqlConnection,
      ProductId product,
      List<ProductVariation> variations
  ) {
    if (variations.isEmpty()) {
      return Uni.createFrom().voidItem();
    }
    return sqlConnection.preparedQuery("""
            INSERT INTO product_variation_attributes (variation_id, product_id, type, value_type, value) VALUES ($1, $2, $3, $4, $5)
            """)
        .executeBatch(toSqlParams(product, variations))
        .replaceWithVoid();
  }

  private static List<Tuple> toSqlParams(ProductId product, List<ProductVariation> variations) {
    return variations.stream()
        .flatMap(variation ->
            variation.attributes().stream()
                .map(attribute -> Tuple.of(
                    variation.id().value(),
                    product.value(),
                    attribute.type().value(),
                    toAttributeValueType(attribute.value()).name(),
                    toAttributeValue(attribute.value())
                ))
        )
        .toList();
  }

  private static ProductVariationAttributeValueType toAttributeValueType(AttributeValue<?> attributeValue) {
    if (attributeValue instanceof IntegerAttributeValue) {
      return ProductVariationAttributeValueType.INTEGER;
    } else if (attributeValue instanceof LongAttributeValue) {
      return ProductVariationAttributeValueType.LONG;
    } else if (attributeValue instanceof BigDecimalAttributeValue) {
      return ProductVariationAttributeValueType.BIG_DECIMAL;
    } else {
      return ProductVariationAttributeValueType.STRING;
    }
  }

  private static String toAttributeValue(AttributeValue<?> attributeValue) {
    if (isNull(attributeValue)) {
      return null;
    } else if (attributeValue instanceof IntegerAttributeValue integerAttributeValue) {
      return String.valueOf(integerAttributeValue.value());
    } else if (attributeValue instanceof LongAttributeValue longAttributeValue) {
      return String.valueOf(longAttributeValue.value());
    } else if (attributeValue instanceof BigDecimalAttributeValue bigDecimalAttributeValue) {
      return String.valueOf(bigDecimalAttributeValue.value());
    } else {
      return attributeValue.value().toString();
    }
  }
}
