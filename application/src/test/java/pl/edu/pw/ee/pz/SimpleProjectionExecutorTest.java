package pl.edu.pw.ee.pz;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.enterprise.inject.Instance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.Projection;
import pl.edu.pw.ee.pz.sharedkernel.model.Timestamp;

class SimpleProjectionExecutorTest {

  @Test
  void should_handle_known_event() {
    // given
    var projection = new AnyProjection(List.of(AnyEvent.class));
    var subjectUnderTest = new SimpleProjectionExecutor(projectionsInstances(Stream.of(projection)));
    var event = new AnyEvent();

    // when
    subjectUnderTest.execute(event)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .assertCompleted();

    // then
    assertThat(projection.handledEvents()).containsExactly(event);
  }

  @Test
  void should_handle_known_event_using_appropriate_projection() {
    // given
    var projection = new AnyProjection(List.of(AnyEvent.class));
    var otherProjection = new AnyProjection(List.of(UnhandledEvent.class));
    var subjectUnderTest = new SimpleProjectionExecutor(projectionsInstances(Stream.of(projection, otherProjection)));
    var event = new AnyEvent();

    // when
    subjectUnderTest.execute(event)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .assertCompleted();

    // then
    assertThat(projection.handledEvents()).containsExactly(event);
  }

  @Test
  void should_handle_known_event_with_multiple_projections_supporting_the_same_event() {
    // given
    var projection = new AnyProjection(List.of(AnyEvent.class));
    var otherProjection = new AnyProjection(List.of(AnyEvent.class));
    var subjectUnderTest = new SimpleProjectionExecutor(projectionsInstances(Stream.of(projection, otherProjection)));
    var event = new AnyEvent();

    // when
    subjectUnderTest.execute(event)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .assertCompleted();

    // then
    assertThat(projection.handledEvents()).containsExactly(event);
    assertThat(otherProjection.handledEvents()).containsExactly(event);
  }

  @Test
  void should_not_handle_unknown_event() {
    // given
    var projection = new AnyProjection(List.of(AnyEvent.class));
    var subjectUnderTest = new SimpleProjectionExecutor(projectionsInstances(Stream.of(projection)));
    var event = new UnhandledEvent();

    // when
    subjectUnderTest.execute(event)
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .assertCompleted();

    // then
    assertThat(projection.handledEvents()).isEmpty();
  }

  @Test
  void should_instantiate_no_projection_executor() {
    // when
    var subjectUnderTest = new SimpleProjectionExecutor(projectionsInstances(Stream.of()));

    // then
    assertThat(subjectUnderTest).isNotNull();

    // and
    subjectUnderTest.execute(new AnyEvent())
        .subscribe().withSubscriber(UniAssertSubscriber.create())
        .assertCompleted();
  }

  @Test
  void should_fail_on_execution_error() {
    // given
    var projection = new Projection() {
      @Override
      public Uni<Void> handle(DomainEvent<?> event) {
        return Uni.createFrom().failure(new UnsupportedOperationException("Not yet implemented"));
      }

      @Override
      public List<Class<? extends DomainEvent<?>>> supportedEvents() {
        return List.of(AnyEvent.class);
      }
    };
    var subjectUnderTest = new SimpleProjectionExecutor(projectionsInstances(Stream.of(projection)));
    var event = new AnyEvent();

    // when
    var tester = subjectUnderTest.execute(event)
        .subscribe().withSubscriber(UniAssertSubscriber.create());

    // then
    var failures = (CompositeException) tester.assertFailedWith(CompositeException.class)
        .getFailure();
    assertThat(failures.getCauses())
        .singleElement()
        .satisfies(failure -> {
          assertThat(failure).isInstanceOf(UnsupportedOperationException.class);
          assertThat(failure.getMessage()).isEqualTo("Not yet implemented");
        });
  }

  private Instance<Projection> projectionsInstances(Stream<Projection> projections) {
    @SuppressWarnings("unchecked")
    var instance = (Instance<Projection>) mock(Instance.class);
    BDDMockito.given(instance.stream())
        .willReturn(projections);
    return instance;
  }

  @RequiredArgsConstructor(access = PRIVATE)
  private static class AnyProjection implements Projection {

    @Getter
    @Accessors(fluent = true)
    private final List<Class<? extends DomainEvent<?>>> supportedEvents;
    @Getter(PRIVATE)
    @Accessors(fluent = true)
    private final List<DomainEvent<?>> handledEvents = new ArrayList<>();

    @Override
    public Uni<Void> handle(DomainEvent<?> event) {
      handledEvents.add(event);
      return Uni.createFrom().voidItem();
    }
  }

  private static abstract class SimpleEvent implements DomainEvent<AggregateId> {

    private static final AtomicLong ID_SEQUENCE = new AtomicLong();
    @Getter
    @Accessors(fluent = true)
    private final DomainEventHeader<AggregateId> header = new DomainEventHeader<>(
        new EventId(ID_SEQUENCE.incrementAndGet()),
        () -> UUID.randomUUID().toString(),
        Timestamp.now()
    );
  }

  private static class AnyEvent extends SimpleEvent {

  }

  private static class UnhandledEvent extends SimpleEvent {

  }
}