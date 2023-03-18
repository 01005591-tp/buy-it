package pl.edu.pw.ee.pz.store;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.store.error.InvalidProductVariationPiecesException;

class ProductVariationPiecesTest {

  @Test
  void should_instantiate_product_variation_pieces() {
    // given
    var count = 5L;

    // when
    var pieces = ProductVariationPieces.of(count);

    // then
    assertThat(pieces.count()).isEqualTo(count);
    assertThat(pieces.isNone()).isFalse();
  }

  @Test
  void should_instantiate_product_variation_pieces_with_null_count() {
    // when
    var pieces = ProductVariationPieces.of(null);

    // then
    assertThat(pieces.count()).isEqualTo(0L);
    assertThat(pieces.isNone()).isTrue();
  }

  @Test
  void should_fail_instantiating_when_negative_number() {
    // given
    var count = -1L;

    // when
    var throwableAssert = Assertions.assertThatCode(() -> ProductVariationPieces.of(count));

    // then
    throwableAssert
        .isInstanceOf(InvalidProductVariationPiecesException.class)
        .hasMessage("Pieces count cannot be negative. Requested count was " + count);
  }
}