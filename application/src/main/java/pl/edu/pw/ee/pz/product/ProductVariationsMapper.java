package pl.edu.pw.ee.pz.product;

import static java.util.Objects.isNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeType;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeValue;

@RequiredArgsConstructor
class ProductVariationsMapper {

  List<ProductVariation> toProductVariations(List<Variation> variations) {
    return variations.stream()
        .map(this::toProductVariation)
        .toList();
  }

  private ProductVariation toProductVariation(Variation variation) {
    return new ProductVariation(
        new ProductVariationId(UUID.randomUUID()),
        variation.attributes().stream()
            .map(this::toVariationAttribute)
            .collect(Collectors.toUnmodifiableSet())
    );
  }

  private pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute<?, ?> toVariationAttribute(
      pl.edu.pw.ee.pz.product.VariationAttribute attribute
  ) {
    return new pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute<>(
        new AttributeType(attribute.type()),
        toAttributeValue(attribute)
    );
  }

  private AttributeValue<?> toAttributeValue(pl.edu.pw.ee.pz.product.VariationAttribute attribute) {
    if (isNull(attribute.valueType())) {
      return AttributeValue.stringAttribute(attribute.value());
    }
    return switch (attribute.valueType()) {
      case STRING -> AttributeValue.stringAttribute(attribute.value());
      case LONG -> AttributeValue.longAttribute(Long.parseLong(attribute.value()));
      case INTEGER -> AttributeValue.integerAttribute(Integer.parseInt(attribute.value()));
      case BIG_DECIMAL -> AttributeValue.bigDecimalAttribute(new BigDecimal(attribute.value()));
    };
  }
}
