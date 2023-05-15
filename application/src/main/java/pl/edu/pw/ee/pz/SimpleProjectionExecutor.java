package pl.edu.pw.ee.pz;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toUnmodifiableList;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.inject.Instance;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.Projection;
import pl.edu.pw.ee.pz.sharedkernel.event.ProjectionExecutor;

@Slf4j
class SimpleProjectionExecutor implements ProjectionExecutor {

  private final Map<Class<? extends DomainEvent<?>>, List<Projection>> projectionsByEvent;

  public SimpleProjectionExecutor(Instance<Projection> projectionsInstances) {
    this.projectionsByEvent = projectionsInstances.stream()
        .flatMap(projection -> projection.supportedEvents()
            .stream()
            .map(event -> Map.entry(event, projection))
        )
        .peek(it -> log.info("Registering projection handler for type: {}", it.getKey().getSimpleName()))
        .collect(groupingBy(
            Entry::getKey,
            mapping(Entry::getValue, toUnmodifiableList())
        ));
    if (this.projectionsByEvent.isEmpty()) {
      log.warn("No projection handlers defined");
    }
  }

  @Override
  public Uni<Void> execute(DomainEvent<?> event) {
    var projections = projectionsByEvent.get(event.getClass());
    var eventType = event.getClass().getSimpleName();
    if (isNull(projections)) {
      log.warn("Missing projections for event {}", eventType);
      return Uni.createFrom().voidItem();
    }
    return doHandle(eventType, event, projections);
  }

  private Uni<Void> doHandle(
      String eventType,
      DomainEvent<?> event,
      List<Projection> projections
  ) {
    var stopWatch = new StopWatch();
    var results = projections.stream()
        .map(projection -> handleSingleProjection(eventType, event, projection))
        .collect(collectingAndThen(toUnmodifiableList(), it -> Uni.join().all(it)));
    return results.andCollectFailures()
        .onSubscription().invoke(stopWatch::start)
        .onTermination().invoke((success, failure, cancelled) -> {
          stopWatch.stop();
          if (nonNull(failure)) {
            log.info(
                "Projection event handling of type {} failed after {} [ms] by some projections",
                eventType, stopWatch.getTime(), failure
            );
          } else if (cancelled) {
            log.info(
                "Projection event handling of type {} was cancelled after {} [ms] by some projections",
                eventType, stopWatch.getTime()
            );
          } else {
            log.info(
                "Projection event handling of type {} took {} [ms] and finished successfully by all projections",
                eventType, stopWatch.getTime()
            );
          }
        })
        .replaceWithVoid();
  }

  private static Uni<Void> handleSingleProjection(String eventType, DomainEvent<?> event, Projection projection) {
    var stopWatch = new StopWatch();
    return projection.handle(event)
        .onSubscription().invoke(stopWatch::start)
        .onTermination().invoke((success, failure, cancelled) -> {
          stopWatch.stop();
          if (nonNull(failure)) {
            log.info(
                "Projection event handling of type {} failed after {} [ms] by projection {}",
                eventType, stopWatch.getTime(), projection.getClass().getSimpleName(), failure
            );
          } else if (cancelled) {
            log.info(
                "Projection event handling of type {} was cancelled after {} [ms] by projection {}",
                eventType, stopWatch.getTime(), projection.getClass().getSimpleName()
            );
          } else {
            log.info(
                "Projection event handling of type {} took {} [ms] and finished successfully by projection {}",
                eventType, stopWatch.getTime(), projection.getClass().getSimpleName()
            );
          }
        });
  }
}
