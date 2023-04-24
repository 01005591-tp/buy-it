package pl.edu.pw.ee.pz.sharedkernel.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public final class CompletableFutureUtil {

  private CompletableFutureUtil() {
    throw new UnsupportedOperationException("Cannot instantiate utility class.");
  }

  public static <R> CompletableFuture<R> callAsync(Callable<R> callable) {
    return CompletableFuture.supplyAsync(() -> wrapCallable(callable));
  }

  public static <R> CompletableFuture<R> callAsync(Callable<R> callable, Executor executor) {
    return CompletableFuture.supplyAsync(() -> wrapCallable(callable), executor);
  }

  private static <R> R wrapCallable(Callable<R> callable) {
    try {
      return callable.call();
    } catch (Error | RuntimeException error) { // do not wrap Errors or RuntimeExceptions
      throw error;
    } catch (Throwable throwable) {
      throw new CompletionException(throwable);
    }
  }
}
