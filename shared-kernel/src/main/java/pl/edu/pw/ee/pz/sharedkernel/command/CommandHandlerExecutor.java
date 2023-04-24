package pl.edu.pw.ee.pz.sharedkernel.command;

import io.smallrye.mutiny.Uni;

public interface CommandHandlerExecutor {

  Uni<Void> execute(Command command);
}
