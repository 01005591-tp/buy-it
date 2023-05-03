package pl.edu.pw.ee.pz.brand;

import io.smallrye.mutiny.Uni;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.ee.pz.brand.event.BrandCodeChanged;
import pl.edu.pw.ee.pz.brand.event.BrandCreated;
import pl.edu.pw.ee.pz.brand.port.BrandAggregatePort;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.Projection;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;

@Slf4j
@RequiredArgsConstructor
class BrandProjection implements Projection {

  private final BrandProjectionPort brandProjectionPort;
  private final BrandAggregatePort brandAggregatePort;

  @Override
  public List<Class<? extends DomainEvent<?>>> supportedEvents() {
    return List.of(
        BrandCreated.class,
        BrandCodeChanged.class
    );
  }

  @Override
  public Uni<Void> handle(DomainEvent<?> event) {
    if (event instanceof BrandCreated brandCreated) {
      return handle(brandCreated)
          .onFailure().call(throwable -> recover(brandCreated, throwable));
    } else if (event instanceof BrandCodeChanged codeChanged) {
      return handle(codeChanged)
          .onFailure().call(throwable -> recover(codeChanged, throwable));
    }
    return Uni.createFrom().voidItem();
  }

  private Uni<Void> handle(BrandCreated event) {
    var brand = new Brand(
        event.header().aggregateId(),
        event.code()
    );
    return brandProjectionPort.save(brand)
        .onItem().invoke(() -> log.info("Brand stored {}", brand.id().id()));
  }

  private Uni<Void> handle(BrandCodeChanged event) {
    return brandProjectionPort.findById(event.header().aggregateId())
        .onItem().transformToUni(brand -> brandProjectionPort.save(brand.withCode(event.code()))
            .onItem().invoke(() -> log.info("Brand stored {} with code changed to {}", brand.id().id(), event.code()))
        );
  }

  private Uni<Void> recover(DomainEvent<BrandId> event, Throwable throwable) {
    log.warn("Could not process event {}", event, throwable);
    var brandId = event.header().aggregateId();
    return brandAggregatePort.findById(brandId)
        .onItem().transform(brandAggregate -> new Brand(brandId, brandAggregate.code()))
        .onItem().transformToUni(brandProjectionPort::save)
        .onItem().invoke(() -> log.info("Recovered from error during event processing {}", event));
  }
}
