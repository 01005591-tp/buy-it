package pl.edu.pw.ee.pz;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Functions.TriConsumer;
import jakarta.enterprise.inject.Instance;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandlerExecutor;

@Slf4j
class SimpleCommandHandlerExecutor implements CommandHandlerExecutor {
    private final Map<Class<? extends Command>, CommandHandler<?, ?>> handlers;

  SimpleCommandHandlerExecutor(Instance<CommandHandler<?, ?>> handlersInstances) {
    this.handlers = handlersInstances.stream()
        .peek(it -> log.info("Registering command handler for type: {}", it.commandType().getSimpleName()))
        .collect(toMap(CommandHandler::commandType, Function.identity()));
    if (handlers.isEmpty()) {
      log.warn("No command handlers defined");
    }
  }

  @Override
  public <R> Uni<R> execute(Command command) {
    var handler = handlers.get(command.getClass());
    var commandType = command.getClass().getSimpleName();
    if (isNull(handler)) {
      log.warn("Missing handler for command {}", commandType);
      return Uni.createFrom().item(() -> null);
    }
    return doHandle(commandType, command, handler);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private <R> Uni<R> doHandle(
      String commandType,
      Command command,
      CommandHandler handler
  ) {
    var stopWatch = new StopWatch();
    return handler.handle(command)
        .onSubscription().invoke(stopWatch::start)
        .onTermination().invoke((TriConsumer<R, Throwable, Boolean>) (success, failure, cancelled) -> {
          stopWatch.stop();
          if (nonNull(failure)) {
            log.info(
                "Command handling of type {} failed after {} [ms]",
                commandType, stopWatch.getTime(), failure
            );
          } else if (cancelled) {
            log.info(
                "Command handling of type {} was cancelled after {} [ms]",
                commandType, stopWatch.getTime()
            );
          } else {
            log.info(
                "Command handling of type {} took {} [ms] and finished successfully",
                commandType, stopWatch.getTime()
            );
          }
        });
  }
}
