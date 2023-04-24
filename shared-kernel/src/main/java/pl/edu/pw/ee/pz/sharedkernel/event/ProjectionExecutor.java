package pl.edu.pw.ee.pz.sharedkernel.event;

import io.smallrye.mutiny.Uni;

public interface ProjectionExecutor {

  Uni<Void> execute(DomainEvent<?> event);
}
