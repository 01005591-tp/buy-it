package pl.edu.pw.ee.pz.sharedkernel.event;

import io.smallrye.mutiny.Uni;
import java.util.List;

public interface Projection {

  List<Class<? extends DomainEvent<?>>> supportedEvents();

  Uni<Void> handle(DomainEvent<?> event);

}
