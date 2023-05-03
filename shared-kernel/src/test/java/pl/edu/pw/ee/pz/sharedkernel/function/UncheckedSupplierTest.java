package pl.edu.pw.ee.pz.sharedkernel.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class UncheckedSupplierTest {

  @Test
  void should_supply_value_when_no_exception_thrown() {
    // given
    var supplier = UncheckedSupplier.from(() -> "Hello");

    // when
    var result = supplier.get();

    // then
    assertThat(result).isEqualTo("Hello");
  }

  @Test
  void should_rethrow_original_exception() {
    // given
    var supplier = UncheckedSupplier.from(() -> {
      throw new Exception("Checked exception");
    });

    // when
    var throwableAssert = assertThatCode(supplier::get);

    // then
    throwableAssert
        .isInstanceOf(Exception.class)
        .hasMessage("Checked exception");
  }
}