package pl.edu.pw.ee.pz.product;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.shared.Variation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeType;

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
            .collect(Collectors.toUnmodifiableList())
    );
  }

  private pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute<?, ?> toVariationAttribute(
      pl.edu.pw.ee.pz.shared.Attribute<?, ?> attribute
  ) {
    return new pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute<>(
        new AttributeType(attribute.type()),
        attribute.value().toDomainAttributeValue()
    );
  }
}
