package pl.edu.pw.ee.pz.sharedkernel.function;

import java.util.function.Supplier;

public interface UncheckedSupplier<T> extends Supplier<T> {

  default T get() {
    try {
      return getExceptionally();
    } catch (Exception e) {
      return ExceptionUtil.sneakyThrow(e);
    }
  }

  T getExceptionally() throws Exception;

  static <T> UncheckedSupplier<T> from(UncheckedSupplier<T> supplier) {
    return supplier;
  }
}
