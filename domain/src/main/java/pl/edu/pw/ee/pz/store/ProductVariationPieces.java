package pl.edu.pw.ee.pz.store;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.sharedkernel.model.FluentComparable;
import pl.edu.pw.ee.pz.store.error.InvalidProductVariationPiecesException;

@Value
@Accessors(fluent = true)
@RequiredArgsConstructor(access = PRIVATE)
public final class ProductVariationPieces implements FluentComparable<ProductVariationPieces> {

  private static final ProductVariationPieces NONE = new ProductVariationPieces(0L);
  private final Long count;

  public static ProductVariationPieces of(Long count) {
    if (isNull(count)) {
      return NONE;
    } else if (count < 0) {
      throw InvalidProductVariationPiecesException.negativeCount(count);
    } else {
      return new ProductVariationPieces(count);
    }
  }

  public ProductVariationPieces add(ProductVariationPieces augend) {
    return new ProductVariationPieces(count + augend.count());
  }

  public ProductVariationPieces subtract(ProductVariationPieces subtrahend) {
    return new ProductVariationPieces(count - subtrahend.count());
  }

  public boolean isNone() {
    return count == 0;
  }

  @Override
  public int compareTo(ProductVariationPieces other) {
    return Long.compare(this.count, other.count());
  }

  public static ProductVariationPieces none() {
    return NONE;
  }
}
