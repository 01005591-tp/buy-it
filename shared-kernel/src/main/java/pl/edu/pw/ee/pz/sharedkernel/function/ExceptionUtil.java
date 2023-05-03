package pl.edu.pw.ee.pz.sharedkernel.function;

import java.util.concurrent.Callable;

public final class ExceptionUtil {

  private ExceptionUtil() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }

  public static <E extends Throwable, R> R sneakyThrow(Callable<R> callable) throws E {
    try {
      return callable.call();
    } catch (Exception exception) {
      @SuppressWarnings("unchecked")
      var thrown = (E) exception;
      throw thrown;
    }
  }

  public static <E extends Throwable, R> R sneakyThrow(Throwable throwable) throws E {
    @SuppressWarnings("unchecked")
    var thrown = (E) throwable;
    throw thrown;
  }
}
