package pl.edu.pw.ee.pz.product;

import static java.util.stream.Collectors.groupingBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import pl.edu.pw.ee.pz.query.PageRecord;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeType;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeValue;

class ProductDbMapper {

  private static final ProductVariationId EMPTY_PRODUCT_VARIATION_ID = new ProductVariationId(null);

  Product toProduct(List<ProductVariationAttributeView> productWithAttributes) {
    return toProduct(productWithAttributes.get(0), productWithAttributes);
  }

  PageRecord<Product> toProductPageRecord(
      PageRecord<String> productPageRecord,
      List<ProductVariationAttributeView> productWithAttributes
  ) {
    var first = productWithAttributes.get(0);
    var product = toProduct(first, productWithAttributes);
    return productPageRecord.withValue(product);
  }

  private Product toProduct(ProductVariationAttributeView first, List<ProductVariationAttributeView> attributes) {
    var productId = new ProductId(UUID.fromString(first.productId()));
    var code = new ProductCode(first.productCode());
    var brand = new BrandId(UUID.fromString(first.brandId()));
    Set<ProductVariation> productVariations = toProductVariations(attributes);
    return new Product(
        productId,
        code,
        brand,
        productVariations
    );
  }

  private Set<ProductVariation> toProductVariations(List<ProductVariationAttributeView> productAndVariations) {
    var variations = productAndVariations.stream().collect(groupingBy(ProductVariationAttributeView::variationId));
    return variations.entrySet().stream()
        .map(variation -> new ProductVariation(
            toProductVariationId(variation.getKey()),
            variation.getValue().stream()
                .map(attribute -> new VariationAttribute<>(
                    new AttributeType(attribute.attributeType()),
                    toAttributeValue(attribute)
                ))
                .collect(Collectors.toUnmodifiableList())
        ))
        .collect(Collectors.toUnmodifiableSet());
  }

  private ProductVariationId toProductVariationId(String variationId) {
    return variationId.isBlank()
        ? EMPTY_PRODUCT_VARIATION_ID
        : new ProductVariationId(UUID.fromString(variationId));
  }

  private AttributeValue<?> toAttributeValue(ProductVariationAttributeView attribute) {
    return switch (attribute.attributeValueType()) {
      case STRING -> AttributeValue.stringAttribute(attribute.attributeValue());
      case LONG -> AttributeValue.longAttribute(Long.parseLong(attribute.attributeValue()));
      case INTEGER -> AttributeValue.integerAttribute(Integer.parseInt(attribute.attributeValue()));
      case BIG_DECIMAL -> AttributeValue.bigDecimalAttribute(new BigDecimal(attribute.attributeValue()));
    };
  }
}
