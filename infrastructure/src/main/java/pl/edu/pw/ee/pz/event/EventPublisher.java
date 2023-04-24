package pl.edu.pw.ee.pz.event;

import io.smallrye.mutiny.Uni;
import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;

public interface EventPublisher {

  <ID extends AggregateId> Uni<Void> publish(List<DomainEvent<ID>> events);
}
