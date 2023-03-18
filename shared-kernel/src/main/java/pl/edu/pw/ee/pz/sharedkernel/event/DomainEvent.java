package pl.edu.pw.ee.pz.sharedkernel.event;

import pl.edu.pw.ee.pz.sharedkernel.model.Timestamp;

public interface DomainEvent {

  DomainEventHeader eventHeader();

  record DomainEventHeader(
      EventId id,
      Timestamp timestamp
  ) {

  }

  record EventId(Long value) {

    private static final EventId EMPTY = new EventId(0L);

    public EventId next() {
      return new EventId(value + 1);
    }

    public static EventId initial() {
      return EMPTY;
    }
  }
}
