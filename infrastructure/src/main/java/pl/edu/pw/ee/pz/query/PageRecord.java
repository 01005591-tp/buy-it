package pl.edu.pw.ee.pz.query;

import io.vertx.mutiny.sqlclient.Row;

public record PageRecord<T>(
    long elementId,
    long allCount,
    T value
) {

  public <V> PageRecord<V> withValue(V value) {
    return new PageRecord<>(elementId, allCount, value);
  }

  public static <T> PageRecord<T> of(Row row, T value) {
    return new PageRecord<>(
        row.getLong("keyset_id"),
        row.getLong("all_count"),
        value
    );
  }

}
