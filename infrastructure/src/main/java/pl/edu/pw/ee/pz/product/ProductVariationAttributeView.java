package pl.edu.pw.ee.pz.product;

import io.vertx.mutiny.sqlclient.Row;
import java.util.Objects;

record ProductVariationAttributeView(
    String productId,
    String productCode,
    String brandId,
    String variationId,
    String attributeType,
    String attributeValue,
    ProductVariationAttributeValueType attributeValueType
) {

  static ProductVariationAttributeView ofRow(Row row) {
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
}
