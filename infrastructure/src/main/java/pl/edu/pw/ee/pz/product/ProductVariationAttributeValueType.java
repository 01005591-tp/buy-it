package pl.edu.pw.ee.pz.product;

import static java.util.Objects.isNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

enum ProductVariationAttributeValueType {
  STRING,
  LONG,
  INTEGER,
  BIG_DECIMAL;
  private static final Map<String, ProductVariationAttributeValueType> NAME_VALUE_MAPPING = Arrays.stream(
          ProductVariationAttributeValueType.values())
      .collect(Collectors.toMap(Enum::name, Function.identity()));

  static Optional<ProductVariationAttributeValueType> tryParse(String value) {
    if (isNull(value) || value.isBlank()) {
      return Optional.empty();
    }
    return Optional.ofNullable(NAME_VALUE_MAPPING.get(value));
  }
}
