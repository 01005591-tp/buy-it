package pl.edu.pw.ee.pz.sharedkernel.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class UncheckedFunctionTest {

  @Test
  void should_supply_value_when_no_exception_thrown() {
    // given
    var supplier = UncheckedFunction.from(String::length);

    // when
    var result = supplier.apply("Hello");

    // then
    assertThat(result).isEqualTo(5);
  }

  @Test
  void should_rethrow_original_exception() {
    // given
    var supplier = UncheckedFunction.from(ignore -> {
      throw new Exception("Checked exception");
    });

    // when
    var throwableAssert = assertThatCode(() -> supplier.apply("Anything"));

    // then
    throwableAssert
        .isInstanceOf(Exception.class)
        .hasMessage("Checked exception");
  }
}