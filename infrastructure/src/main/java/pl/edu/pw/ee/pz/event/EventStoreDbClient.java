package pl.edu.pw.ee.pz.event;

import static lombok.AccessLevel.PACKAGE;

import com.eventstore.dbclient.AppendToStreamOptions;
import com.eventstore.dbclient.EventData;
import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.ExpectedRevision;
import com.eventstore.dbclient.ReadMessage;
import com.eventstore.dbclient.ReadStreamOptions;
import com.eventstore.dbclient.ResolvedEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.FlowAdapters;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRoot;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateType;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.EventSerializer;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class EventStoreDbClient {

  private final EventSerializer eventSerializer;
  private final EventStoreDBClient client;

  public <ID extends AggregateId> Uni<List<AggregateDomainEvent<ID>>> findEventsForAggregate(
      AggregateType type,
      ID id
  ) {
    var readOptions = ReadStreamOptions.get()
        .fromStart();
    return Multi.createFrom()
        .publisher(FlowAdapters.toFlowPublisher(client.readStreamReactive(streamName(type, id), readOptions)))
        .select().where(ReadMessage::hasEvent)
        .onItem().transform(ReadMessage::getEvent)
        .onItem().<AggregateDomainEvent<ID>>transform(this::deserializeEvent)
        .collect().asList();
  }

  public <ID extends AggregateId, A extends AggregateRoot<ID>> Uni<Void> saveEvents(A aggregateRoot) {
    var inEvents = aggregateRoot.getAndClearPendingInEvents().stream()
        .map(event ->
            EventData.builderAsBinary(event.getClass().getName(), eventSerializer.serialize(event)).build()
        )
        .iterator();
    return Uni.createFrom()
        .completionStage(client.appendToStream(
            streamName(aggregateRoot),
            AppendToStreamOptions.get().expectedRevision(expectedRevision(aggregateRoot.version())),
            inEvents
        ))
        .onItem().invoke(it -> log.info("Events saved for aggregate {} {}. Next expected revision is {}",
            aggregateRoot.type().value(), aggregateRoot.id().value(), it.getNextExpectedRevision()
        ))
        .replaceWithVoid();
  }

  private <ID extends AggregateId, A extends AggregateRoot<ID>> ExpectedRevision expectedRevision(
      Version version
  ) {
    return version.isInitial()
        ? ExpectedRevision.noStream()
        : ExpectedRevision.expectedRevision(version.value());
  }

  private <ID extends AggregateId, A extends AggregateRoot<ID>> String streamName(A aggregateRoot) {
    return streamName(aggregateRoot.type(), aggregateRoot.id());
  }

  private String streamName(AggregateType type, AggregateId id) {
    return "%s-%s".formatted(type.value(), id.value());
  }

  private <ID extends AggregateId, E extends DomainEvent<ID>> AggregateDomainEvent<ID> deserializeEvent(
      ResolvedEvent resolvedEvent
  ) {
    var event = eventSerializer.<ID, E>deserialize(resolvedEvent.getEvent().getEventData());
    return new AggregateDomainEvent<>(event, resolvedEvent.getEvent().getRevision());
  }
}
