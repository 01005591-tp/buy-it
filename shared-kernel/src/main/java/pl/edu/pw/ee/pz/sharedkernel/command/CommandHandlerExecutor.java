package pl.edu.pw.ee.pz.sharedkernel.command;

import io.smallrye.mutiny.Uni;

public interface CommandHandlerExecutor {

  <R> Uni<R> execute(Command command);
}
