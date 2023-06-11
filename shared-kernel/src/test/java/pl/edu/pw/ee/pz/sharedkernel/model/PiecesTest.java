package pl.edu.pw.ee.pz.sharedkernel.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.sharedkernel.model.Pieces.InvalidPiecesCountException;

class PiecesTest {

  @Test
  void should_instantiate_product_variation_pieces() {
    // given
    var count = 5L;

    // when
    var pieces = Pieces.of(count);

    // then
    assertThat(pieces.value()).isEqualTo(count);
    assertThat(pieces.isNone()).isFalse();
  }

  @Test
  void should_instantiate_product_variation_pieces_with_null_count() {
    // when
    var pieces = Pieces.of(null);

    // then
    assertThat(pieces.value()).isEqualTo(0L);
    assertThat(pieces.isNone()).isTrue();
  }

  @Test
  void should_fail_instantiating_when_negative_number() {
    // given
    var count = -1L;

    // when
    var throwableAssert = Assertions.assertThatCode(() -> Pieces.of(count));

    // then
    throwableAssert
        .isInstanceOf(InvalidPiecesCountException.class)
        .hasMessage("Pieces count cannot be negative. Requested count was " + count);
  }
}