package pl.edu.pw.ee.pz.sharedkernel.function;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class MapUtil {

  private MapUtil() {
    throw new UnsupportedOperationException("Cannot instantiate utility class.");
  }

  public static <K, V, R> Function<Map.Entry<K, V>, R> mapEntry(
      BiFunction<? super K, ? super V, ? extends R> mapper
  ) {
    return entry -> mapper.apply(entry.getKey(), entry.getValue());
  }
}
