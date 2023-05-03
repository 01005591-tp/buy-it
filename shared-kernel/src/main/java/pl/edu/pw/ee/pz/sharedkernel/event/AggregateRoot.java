package pl.edu.pw.ee.pz.sharedkernel.event;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.DomainEventHeader;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.Timestamp;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;

@Accessors(fluent = true)
public abstract class AggregateRoot<ID extends AggregateId> {

  @Getter
  protected ID id;
  @Getter
  protected final AggregateType type;
  @Getter
  protected Version version;
  protected EventId latestEvent;
  protected List<DomainEvent<ID>> pendingInEvents = new ArrayList<>();
  protected List<DomainEvent<ID>> pendingOutEvents = new ArrayList<>();

  protected AggregateRoot(
      AggregateType type,
      Version version,
      EventId latestEvent
  ) {
    this.type = type;
    this.version = version;
    this.latestEvent = latestEvent;
  }

  protected void handleAndRegisterEvent(DomainEvent<ID> event) {
    this.pendingInEvents.add(event);
    handle(event);
  }

  protected DomainEventHeader<ID> nextDomainEventHeader() {
    return nextDomainEventHeader(this.id);
  }

  protected DomainEventHeader<ID> nextDomainEventHeader(ID id) {
    this.latestEvent = this.latestEvent.next();
    return new DomainEventHeader<>(latestEvent, id, Timestamp.now());
  }

  protected void registerOutEvent(DomainEvent<ID> event) {
    this.pendingOutEvents.add(event);
  }

  protected abstract void handle(DomainEvent<ID> event);

  public List<DomainEvent<ID>> getAndClearPendingInEvents() {
    var events = pendingInEvents;
    this.pendingInEvents = new ArrayList<>();
    return events;
  }

  public List<DomainEvent<ID>> getAndClearPendingOutEvents() {
    var events = pendingOutEvents;
    this.pendingOutEvents = new ArrayList<>();
    return events;
  }

  public static <ID extends AggregateId, A extends AggregateRoot<ID>> A restore(
      List<DomainEvent<ID>> inEvents,
      Version version,
      AggregateRootEmptyConstructor<A> constructor
  ) {
    var lastEvent = inEvents.get(inEvents.size() - 1);
    var lastEventId = lastEvent.header().id();
    var aggregate = constructor.newInstance(version, lastEventId);
    // handle all events, without storing them in pending events
    inEvents.forEach(aggregate::handle);
    // clear out pending events, all events had already been fired
    aggregate.pendingOutEvents.clear();
    return aggregate;
  }

  public interface AggregateRootEmptyConstructor<A> {

    A newInstance(Version version, EventId latestEvent);
  }
}
