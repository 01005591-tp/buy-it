package pl.edu.pw.ee.pz.event;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRoot;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRoot.AggregateRootEmptyConstructor;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateType;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;

@Slf4j
@RequiredArgsConstructor
public class EventStoreRepository {

  private final EventStoreDbClient eventStoreDbClient;
  private final EventPublisher eventPublisher;

  public <ID extends AggregateId, A extends AggregateRoot<ID>> Uni<A> findById(
      AggregateType type,
      ID id,
      AggregateRootEmptyConstructor<A> constructor
  ) {
    return eventStoreDbClient.findEventsForAggregate(type, id)
        .onItem().transform(events -> AggregateRoot.restore(events, Version.initial(), constructor));
  }

  public <ID extends AggregateId, A extends AggregateRoot<ID>> Uni<Void> save(A aggregateRoot) {
    return eventStoreDbClient.saveEvents(aggregateRoot)
        .onItem().transformToUni(success ->
            eventPublisher.publish(aggregateRoot.getAndClearPendingOutEvents())
        )
        .onItem().invoke(success -> log.info(
            "Aggregate root {} {} saved.",
            aggregateRoot.type().value(),
            aggregateRoot.id().value()
        ))
        .onFailure().invoke(failure -> log.error(
            "Could not save aggregate root {} {}.",
            aggregateRoot.type().value(),
            aggregateRoot.id().value(),
            failure
        ));
  }
}
