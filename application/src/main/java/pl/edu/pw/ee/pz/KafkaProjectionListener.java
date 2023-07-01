package pl.edu.pw.ee.pz;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.smallrye.reactive.messaging.kafka.KafkaRecordBatch;
import java.util.concurrent.CompletionStage;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import pl.edu.pw.ee.pz.sharedkernel.event.EventSerializer;
import pl.edu.pw.ee.pz.sharedkernel.event.ProjectionExecutor;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class KafkaProjectionListener {

  private final ProjectionExecutor projectionExecutor;
  private final EventSerializer eventSerializer;

  @Incoming("domain-events-in")
  public CompletionStage<Void> consumeDomainEvent(KafkaRecordBatch<String, byte[]> records) {
    // TODO: ensure consumer idempotence (do not change projection based on the same event more than once)
    return StreamSupport.stream(records.spliterator(), false)
        .map(this::execute)
        .reduce(
            (first, second) -> Uni.join().all(first, second).andCollectFailures().replaceWithVoid()
        )
        .orElseGet(() -> Uni.createFrom().voidItem())
        .onItem().transformToUni(success -> Uni.createFrom().completionStage(records.ack()))
        .subscribeAsCompletionStage();
  }

  private Uni<Void> execute(KafkaRecord<String, byte[]> record) {
    return Uni.createFrom()
        .deferred(() -> projectionExecutor.execute(eventSerializer.deserialize(record.getPayload())));
  }
}
