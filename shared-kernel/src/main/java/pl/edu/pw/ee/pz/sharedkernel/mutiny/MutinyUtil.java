package pl.edu.pw.ee.pz.sharedkernel.mutiny;

import io.smallrye.mutiny.Uni;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public final class MutinyUtil {

  private MutinyUtil() {
    throw new UnsupportedOperationException("Cannot instantiate utility class.");
  }

  public static <R> Uni<R> uniFromCallable(Callable<R> callable) {
    return Uni.createFrom().item(toSupplier(callable));
  }

  private static <R> Supplier<R> toSupplier(Callable<R> callable) {
    return () -> {
      try {
        return callable.call();
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }
    };
  }

  public static <R> Uni<R> uniFromCompletionStageCallable(Callable<CompletionStage<R>> completionStageCallable) {
    return uniFromCallable(completionStageCallable)
        .onItem().transformToUni(completionStage -> Uni.createFrom().completionStage(completionStage));
  }
}
