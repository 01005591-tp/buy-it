package pl.edu.pw.ee.pz.sharedkernel.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.concurrent.Callable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ExceptionUtilTest {

  @Nested
  class On_callable {

    @Test
    void should_return_value_when_no_exception_thrown() {
      // given
      var callable = (Callable<String>) () -> "Hello";

      // when
      var result = ExceptionUtil.sneakyThrow(callable);

      // then
      assertThat(result).isEqualTo("Hello");
    }

    @Test
    void should_rethrow_original_exception() {
      // given
      var callable = (Callable<String>) () -> {
        throw new Exception("Checked exception");
      };

      // when
      var throwableAssert = assertThatCode(() -> ExceptionUtil.sneakyThrow(callable));

      // then
      throwableAssert
          .isInstanceOf(Exception.class)
          .hasMessage("Checked exception");
    }
  }

  @Nested
  class On_throwable {

    @Test
    void should_rethrow_original_exception() {
      // given
      var throwable = new Exception("Checked exception");

      // when
      var throwableAssert = assertThatCode(() -> ExceptionUtil.sneakyThrow(throwable));

      // then
      throwableAssert
          .isInstanceOf(Exception.class)
          .hasMessage("Checked exception");
    }
  }
}