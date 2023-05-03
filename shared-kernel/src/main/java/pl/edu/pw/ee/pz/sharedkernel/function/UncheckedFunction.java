package pl.edu.pw.ee.pz.sharedkernel.function;

import java.util.function.Function;

public interface UncheckedFunction<T, R> extends Function<T, R> {

  @Override
  default R apply(T t) {
    try {
      return applyExceptionally(t);
    } catch (Throwable throwable) {
      return ExceptionUtil.sneakyThrow(throwable);
    }
  }

  R applyExceptionally(T t) throws Throwable;

  static <T, R> UncheckedFunction<T, R> from(UncheckedFunction<T, R> function) {
    return function;
  }
}
