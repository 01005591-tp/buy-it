package pl.edu.pw.ee.pz.sharedkernel.function;

import java.util.function.Supplier;

public interface UncheckedSupplier<T> extends Supplier<T> {

  default T get() {
    try {
      return getExceptionally();
    } catch (Throwable throwable) {
      return ExceptionUtil.sneakyThrow(throwable);
    }
  }

  T getExceptionally() throws Throwable;

  static <T> UncheckedSupplier<T> from(UncheckedSupplier<T> supplier) {
    return supplier;
  }
}
