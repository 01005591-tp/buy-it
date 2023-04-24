package pl.edu.pw.ee.pz.event;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaClientService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.EventSerializer;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class KafkaEventPublisher implements EventPublisher {

  private final EventSerializer eventSerializer;
  private final KafkaClientService kafkaClientService;
  private final String topic;
  private final DomainEventsProperties domainEventsProperties;

  @Override
  public <ID extends AggregateId> Uni<Void> publish(List<DomainEvent<ID>> events) {
    if (events.isEmpty()) {
      return Uni.createFrom().voidItem();
    }
    var producer = kafkaClientService.<String, byte[]>getProducer("domain-events-out");
    var unis = events.stream()
        .map(event -> new EventProducerRecord<>(
            new ProducerRecord<>(
                topic,
                event.header().aggregateId().value(),
                eventSerializer.serialize(event)
            ),
            event
        ))
        // @formatter:off
        .map(record ->
            producer.send(record.record())
                .ifNoItem()
                  .after(domainEventsProperties.publishTimeout())
                  .fail()
                .onFailure()
                  .retry()
                    .withBackOff(domainEventsProperties.backoffTimeout())
                    .atMost(domainEventsProperties.maxRetries())
                .onFailure()
                  .invoke(failure -> log.error("Could not publish domain event {}: {}", record.record().key(), record.event(), failure))
                .onItem()
                  .invoke(recordMetadata -> log.info("Published domain event {}: {}", record.record().key(), record.event()))
                .replaceWithVoid()
        )
        // @formatter:on
        .toList();
    return Uni.join().all(unis).andCollectFailures().replaceWithVoid();
  }

  private record EventProducerRecord<ID extends AggregateId>(
      ProducerRecord<String, byte[]> record,
      DomainEvent<ID> event
  ) {

  }
}
