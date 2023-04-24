package pl.edu.pw.ee.pz.sharedkernel.event;

import io.smallrye.mutiny.Uni;
import java.util.List;

public interface Projection {

  Uni<Void> handle(DomainEvent<?> event);

  List<Class<? extends DomainEvent<?>>> supportedEvents();

}
