package pl.edu.pw.ee.pz.util;

import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.ReadAllOptions;
import io.quarkus.test.junit.callback.QuarkusTestBeforeEachCallback;
import io.quarkus.test.junit.callback.QuarkusTestMethodContext;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    var truncateTables = Stream.of("products", "brands")
        .map(table -> pgPool.preparedQuery("TRUNCATE TABLE %s".formatted(table)).execute())
        .toList();
    Uni.join().all(truncateTables)
        .andFailFast()
        .onFailure().invoke(throwable -> log.error("Could not clean up database before tests", throwable))
        .await().atMost(Duration.ofSeconds(10L));
  }

  private void cleanUpEventStoreDb(EventStoreDBClient eventStoreDBClient) {
    Multi.createFrom().publisher(eventStoreDBClient.readAllReactive(ReadAllOptions.get()
            .fromEnd()
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
