package pl.edu.pw.ee.pz.shared;

import java.util.stream.Collectors;
import pl.edu.pw.ee.pz.product.Product;
import pl.edu.pw.ee.pz.shared.Attribute.AttributeValue;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

public class ProductDtoMapper {

  public ProductDto toProductDto(Product product) {
    return new ProductDto(
        product.id().value(),
        product.code().value(),
        product.brand().value(),
        product.variations().stream()
            .map(this::toVariation)
            .toList()
    );
  }

  private Variation toVariation(ProductVariation variation) {
    return new Variation(
        variation.id().value(),
        variation.attributes().stream()
            .map(attribute -> new Attribute<>(
                attribute.type().value(),
                AttributeValue.of(attribute.value())
            ))
            .collect(Collectors.toUnmodifiableList())
    );
  }
}
