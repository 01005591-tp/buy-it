package pl.edu.pw.ee.pz.util;

import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.ReadAllOptions;
import io.quarkus.test.junit.callback.QuarkusTestBeforeEachCallback;
import io.quarkus.test.junit.callback.QuarkusTestMethodContext;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.FlowAdapters;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CleanUpResourcesBeforeEachCallback implements QuarkusTestBeforeEachCallback {

  @Override
  public void beforeEach(QuarkusTestMethodContext context) {
    var pgPool = CDI.current().select(PgPool.class).get();
    var eventStoreDBClient = CDI.current().select(EventStoreDBClient.class).get();
    cleanUpPostgres(pgPool);
    cleanUpEventStoreDb(eventStoreDBClient);
  }

  private void cleanUpPostgres(PgPool pgPool) {
    cleanUpTables(pgPool);
    resetSequences(pgPool);
  }

  private void cleanUpTables(PgPool pgPool) {
    var tables = List.of(
        "products",
        "brands",
        "product_variation_attributes",
        "stores",
        "store_product_pieces"
    );
    var truncateTables = tables.stream()
        .map(table -> pgPool.preparedQuery("TRUNCATE TABLE %s".formatted(table)).execute())
        .toList();
    Uni.join().all(truncateTables)
        .andFailFast()
        .onFailure().invoke(throwable -> log.error("Could not clean up database before tests", throwable))
        .onItem().invoke(() -> {
          if (log.isDebugEnabled()) {
            log.debug("Cleaned up tables {}", String.join(",", tables));
          }
        })
        .await().atMost(Duration.ofSeconds(10L));
  }

  private void resetSequences(PgPool pgPool) {
    var sequences = List.of(
        "products_seq",
        "brands_seq"
    );
    var resetSequences = sequences.stream()
        .map(table -> pgPool.preparedQuery("ALTER SEQUENCE %s RESTART WITH 1".formatted(table)).execute())
        .toList();
    Uni.join().all(resetSequences)
        .andFailFast()
        .onFailure().invoke(throwable -> log.error("Could not reset sequences before tests", throwable))
        .onItem().invoke(() -> {
          if (log.isDebugEnabled()) {
            log.debug("Rest sequences {}", String.join(",", sequences));
          }
        })
        .await().atMost(Duration.ofSeconds(10L));
  }

  private void cleanUpEventStoreDb(EventStoreDBClient eventStoreDBClient) {
    Multi.createFrom().publisher(FlowAdapters.toFlowPublisher(
            eventStoreDBClient.readAllReactive(ReadAllOptions.get().fromEnd())
        ))
        .onItem().transform(it -> it.getEvent().getEvent().getStreamId())
        .collect()
        .asList()
        .onItem().transformToMulti(streams -> Multi.createFrom().iterable(Set.copyOf(streams)))
        .onItem()
        .transformToMulti(stream -> Multi.createFrom().completionStage(eventStoreDBClient.deleteStream(stream)))
        .merge()
        .onFailure().invoke(throwable -> log.error("Could not clean up database before tests", throwable))
        .collect().asList()
        .await().atMost(Duration.ofSeconds(10L));
  }
}
