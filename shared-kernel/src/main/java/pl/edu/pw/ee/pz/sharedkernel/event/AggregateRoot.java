package pl.edu.pw.ee.pz.sharedkernel.event;

import static lombok.AccessLevel.PACKAGE;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;

public abstract class AggregateRoot {

  @Accessors(fluent = true)
  @Getter(PACKAGE)
  protected Version version;
  protected EventId latestEvent;
  protected List<DomainEvent> pendingInEvents = new ArrayList<>();
  protected List<DomainEvent> pendingOutEvents = new ArrayList<>();

  protected void handleAndRegisterEvent(DomainEvent event) {
    this.pendingInEvents.add(event);
    dispatchAndHandle(event);
  }

  protected abstract void dispatchAndHandle(DomainEvent event);
}
