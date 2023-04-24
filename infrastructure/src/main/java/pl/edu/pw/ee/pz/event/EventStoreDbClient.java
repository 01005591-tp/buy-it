package pl.edu.pw.ee.pz.event;

import static lombok.AccessLevel.PACKAGE;

import com.eventstore.dbclient.EventData;
import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.ReadMessage;
import com.eventstore.dbclient.ReadStreamOptions;
import com.eventstore.dbclient.ResolvedEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.List;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRoot;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateType;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.EventSerializer;

@RequiredArgsConstructor(access = PACKAGE)
class EventStoreDbClient {

  private final EventSerializer eventSerializer;
  private final EventStoreDBClient client;

  public <ID extends AggregateId> Uni<List<DomainEvent<ID>>> findEventsForAggregate(
      AggregateType type,
      ID id
  ) {
    var readOptions = ReadStreamOptions.get()
        .fromStart();
    return Multi.createFrom()
        .publisher(client.readStreamReactive(streamName(type, id), readOptions))
        .select().where(ReadMessage::hasEvent)
        .onItem().transform(ReadMessage::getEvent)
        .onItem().<DomainEvent<ID>>transform(this::deserializeEvent)
        .collect().asList();
  }

  public <ID extends AggregateId, A extends AggregateRoot<ID>> Uni<Void> saveEvents(A aggregateRoot) {
    var inEvents = aggregateRoot.getAndClearPendingInEvents().stream()
        .map(event ->
            EventData.builderAsBinary(event.getClass().getName(), eventSerializer.serialize(event)).build()
        )
        .iterator();
    return Uni.createFrom()
        .completionStage(client.appendToStream(streamName(aggregateRoot), inEvents))
        .replaceWithVoid();
  }

  private <ID extends AggregateId, A extends AggregateRoot<ID>> String streamName(A aggregateRoot) {
    return streamName(aggregateRoot.type(), aggregateRoot.id());
  }

  private String streamName(AggregateType type, AggregateId id) {
    return "%s-%s".formatted(type.value(), id.value());
  }

  private <ID extends AggregateId, E extends DomainEvent<ID>> E deserializeEvent(ResolvedEvent resolvedEvent) {
    return eventSerializer.deserialize(resolvedEvent.getEvent().getEventData());
  }
}
