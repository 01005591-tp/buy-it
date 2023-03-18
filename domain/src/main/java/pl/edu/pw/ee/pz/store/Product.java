package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRoot;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRootUtils;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.DomainEventHeader;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.Timestamp;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;
import pl.edu.pw.ee.pz.store.event.ProductModified;

@Accessors(fluent = true)
public class Product extends AggregateRoot {

  @Getter(PACKAGE)
  private ProductId id;
  @Getter(PACKAGE)
  private ProductName name;
  @Getter(PACKAGE)
  private ProductBrand brand;

  private Product(Version version, EventId latestEvent) {
    this.version = version;
    this.latestEvent = latestEvent;
  }

  public Product(
      ProductId id,
      ProductName name,
      ProductBrand brand,
      Timestamp timestamp
  ) {
    this.latestEvent = EventId.initial();
    var eventHeader = new DomainEventHeader(latestEvent.next(), timestamp);
    var added = new ProductModified(eventHeader, id, name, brand);
    handleAndRegisterEvent(added);
  }

  @Override
  protected void dispatchAndHandle(DomainEvent event) {
    if (event instanceof ProductModified modified) {
      handle(modified);
    }
  }

  private void handle(ProductModified modified) {
    this.id = modified.id();
    this.name = modified.name();
    this.brand = modified.brand();
  }

  static Product restore(List<DomainEvent> inEvents, Version version) {
    return AggregateRootUtils.restore(inEvents, version, Product::new);
  }

  public record ProductId(UUID value) {

  }

  public record ProductName(String value) {

  }

  public record ProductBrand(String value) {

  }
}
