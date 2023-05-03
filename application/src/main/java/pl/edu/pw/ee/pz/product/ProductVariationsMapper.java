package pl.edu.pw.ee.pz.product;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
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
    return variation.attributes().stream()
        .map(this::toVariationAttribute)
        .collect(Collectors.collectingAndThen(Collectors.toUnmodifiableSet(), ProductVariation::new));
  }

  private pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute toVariationAttribute(
      pl.edu.pw.ee.pz.product.VariationAttribute attribute
  ) {
    return new pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute(
        new AttributeType(attribute.type()),
        new AttributeValue(attribute.value())
    );
  }
}
