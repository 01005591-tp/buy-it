package pl.edu.pw.ee.pz.sharedkernel.mutiny;

import io.smallrye.mutiny.Uni;
import java.util.concurrent.Callable;
import pl.edu.pw.ee.pz.sharedkernel.function.ExceptionUtil;

public final class MutinyUtil {

  private MutinyUtil() {
    throw new UnsupportedOperationException("Cannot instantiate utility class.");
  }

  public static <R> Uni<R> uniFromCallable(Callable<R> callable) {
    return Uni.createFrom().item(() -> ExceptionUtil.sneakyThrow(callable));
  }
}
