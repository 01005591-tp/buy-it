package pl.edu.pw.ee.pz.sharedkernel.model;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public record ProductVariation(
    ProductVariationId id,
    // TODO: Must be raw type, because Jackson cannot serialize it properly otherwise
    //       Register custom Jackson serializer to serialize List<VariationAttribute> properly.
    List<VariationAttribute> attributes
) {

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProductVariation that)) {
      return false;
    }
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
