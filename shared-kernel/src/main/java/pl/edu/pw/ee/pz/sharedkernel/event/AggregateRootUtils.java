package pl.edu.pw.ee.pz.sharedkernel.event;

import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;

public class AggregateRootUtils {

  public static <A extends AggregateRoot> A restore(
      List<DomainEvent> inEvents,
      Version version,
      AggregateRootEmptyConstructor<A> constructor
  ) {
    var aggregate = constructor.newInstance(version, inEvents.get(inEvents.size() - 1).eventHeader().id());
    // handle all events, without storing them in pending events
    inEvents.forEach(aggregate::dispatchAndHandle);
    // clear out pending events, all events had already been fired
    aggregate.pendingOutEvents.clear();
    return aggregate;
  }

  public interface AggregateRootEmptyConstructor<A> {

    A newInstance(Version version, EventId latestEvent);
  }

}
