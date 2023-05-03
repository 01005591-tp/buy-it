package pl.edu.pw.ee.pz;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.enterprise.inject.Instance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler.NoResultCommandHandler;

class SimpleCommandHandlerExecutorTest {

  @Test
  void should_handle_known_command() {
    // given
    var handler = new AnyCommandHandler<AnyCommand>(AnyCommand.class);
    var subjectUnderTest = new SimpleCommandHandlerExecutor(handlersInstances(Stream.of(handler)));
    var command = new AnyCommand();

    // when
    subjectUnderTest.execute(command)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .assertCompleted();

    // then
    assertThat(handler.handledCommands()).containsExactly(command);
  }

  @Test
  void should_handle_known_command_using_appropriate_handler() {
    // given
    var handler = new AnyCommandHandler<AnyCommand>(AnyCommand.class);
    var otherHandler = new AnyCommandHandler<UnhandledCommand>(UnhandledCommand.class);
    var subjectUnderTest = new SimpleCommandHandlerExecutor(handlersInstances(Stream.of(handler, otherHandler)));
    var command = new AnyCommand();

    // when
    subjectUnderTest.execute(command)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .assertCompleted();

    // then
    assertThat(handler.handledCommands()).containsExactly(command);
  }

  @Test
  void should_not_handle_unknown_command() {
    // given
    var handler = new AnyCommandHandler<Command>(Command.class);
    var subjectUnderTest = new SimpleCommandHandlerExecutor(handlersInstances(Stream.of(handler)));
    var command = new UnhandledCommand();

    // when
    subjectUnderTest.execute(command)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .assertCompleted();

    // then
    assertThat(handler.handledCommands()).isEmpty();
  }

  @Test
  void should_instantiate_no_handler_executor() {
    // when
    var executor = new SimpleCommandHandlerExecutor(handlersInstances(Stream.empty()));

    // then
    assertThat(executor).isNotNull();

    // and
    executor.execute(new AnyCommand())
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .assertCompleted();
  }

  @Test
  void should_fail_instantiation_on_multiple_handlers_for_the_same_command() {
    // given
    var handler = new AnyCommandHandler<AnyCommand>(AnyCommand.class);
    var otherHandler = new AnyCommandHandler<AnyCommand>(AnyCommand.class);

    // when
    var throwableAssert = assertThatCode(
        () -> new SimpleCommandHandlerExecutor(handlersInstances(Stream.of(handler, otherHandler))));

    // then
    throwableAssert
        .isInstanceOf(IllegalStateException.class)
        .hasMessageStartingWith("Duplicate key ");
  }

  @Test
  void should_fail_on_execution_error() {
    // given
    CommandHandler<AnyCommand, Void> handler = CommandHandler.handler(
        AnyCommand.class,
        cmd -> Uni.createFrom().failure(new UnsupportedOperationException("Not yet implemented"))
    );
    var subjectUnderTest = new SimpleCommandHandlerExecutor(handlersInstances(Stream.of(handler)));
    var command = new AnyCommand();

    // when
    var tester = subjectUnderTest.execute(command)
        .subscribe().withSubscriber(UniAssertSubscriber.create());

    // then
    tester.assertFailedWith(UnsupportedOperationException.class, "Not yet implemented");
  }

  private Instance<CommandHandler<?, ?>> handlersInstances(Stream<CommandHandler<?, ?>> handlers) {
    @SuppressWarnings("unchecked")
    var instance = (Instance<CommandHandler<?, ?>>) mock(Instance.class);
    BDDMockito.given(instance.stream())
        .willReturn(handlers);
    return instance;
  }

  @RequiredArgsConstructor(access = PRIVATE)
  private static class AnyCommandHandler<C extends Command> implements NoResultCommandHandler<C> {

    @Getter
    @Accessors(fluent = true)
    private final Class<C> commandType;

    @Getter(PRIVATE)
    @Accessors(fluent = true)
    private final List<C> handledCommands = new ArrayList<>();

    @Override
    public Uni<Void> handle(C command) {
      handledCommands.add(command);
      return Uni.createFrom().voidItem();
    }
  }

  private record AnyCommand() implements Command {

  }

  private record UnhandledCommand() implements Command {

  }
}