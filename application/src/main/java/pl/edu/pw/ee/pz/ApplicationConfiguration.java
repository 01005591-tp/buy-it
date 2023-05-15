package pl.edu.pw.ee.pz;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler;
import pl.edu.pw.ee.pz.sharedkernel.event.EventSerializer;
import pl.edu.pw.ee.pz.sharedkernel.event.Projection;
import pl.edu.pw.ee.pz.sharedkernel.event.ProjectionExecutor;

@Startup
@ApplicationScoped
public class ApplicationConfiguration {

  @Produces
  SimpleCommandHandlerExecutor simpleCommandHandlerExecutor(Instance<CommandHandler<?, ?>> commandHandlers) {
    return new SimpleCommandHandlerExecutor(commandHandlers);
  }

  @Produces
  SimpleProjectionExecutor simpleProjectionExecutor(Instance<Projection> projections) {
    return new SimpleProjectionExecutor(projections);
  }

  @Produces
  KafkaProjectionListener kafkaProjectionListener(
      ProjectionExecutor projectionExecutor,
      EventSerializer eventSerializer
  ) {
    return new KafkaProjectionListener(projectionExecutor, eventSerializer);
  }
}
