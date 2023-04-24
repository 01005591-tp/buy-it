package pl.edu.pw.ee.pz.brand;

import static lombok.AccessLevel.PACKAGE;

import lombok.Getter;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.brand.event.BrandCreated;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRoot;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateType;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;

@Accessors(fluent = true)
public class BrandAggregate extends AggregateRoot<BrandId> {

  private static final AggregateType AGGREGATE_TYPE = new AggregateType("brand");

  @Getter(PACKAGE)
  private BrandCode code;

  BrandAggregate(Version version, EventId latestEvent) {
    super(AGGREGATE_TYPE, version, latestEvent);
  }

  public BrandAggregate(BrandId id, BrandCode code) {
    this(Version.initial(), EventId.initial());
    var header = nextDomainEventHeader(id);
    var created = new BrandCreated(header, code);
    handleAndRegisterEvent(created);
  }

  @Override
  protected void handle(DomainEvent event) {
    if (event instanceof BrandCreated created) {
      handle(created);
    }
  }

  private void handle(BrandCreated event) {
    this.id = event.header().aggregateId();
    this.code = event.code();
    registerOutEvent(event);
  }

  public static AggregateType aggregateType() {
    return AGGREGATE_TYPE;
  }
}
