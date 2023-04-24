package pl.edu.pw.ee.pz.store;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PACKAGE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRoot;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateType;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation.VariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;
import pl.edu.pw.ee.pz.store.error.InsufficientProductVariationPiecesException;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesAdded;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesRemoved;
import pl.edu.pw.ee.pz.store.event.StoreAddressChanged;
import pl.edu.pw.ee.pz.store.event.StoreCreated;

@Accessors(fluent = true)
public class StoreAggregate extends AggregateRoot<StoreId> {

  private static final AggregateType AGGREGATE_TYPE = new AggregateType("store");
  @Getter(PACKAGE)
  private Address address;
  @Getter(PACKAGE)
  private final Map<ProductId, Map<VariationId, ProductVariationPieces>> products = new HashMap<>();

  StoreAggregate(Version version, EventId latestEvent) {
    super(AGGREGATE_TYPE, version, latestEvent);
  }

  public StoreAggregate(StoreId id, Address address) {
    this(Version.initial(), EventId.initial());
    var eventHeader = nextDomainEventHeader(id);
    var created = new StoreCreated(eventHeader, address);
    handleAndRegisterEvent(created);
  }

  public void changeAddress(Address address) {
    var event = new StoreAddressChanged(
        nextDomainEventHeader(),
        address
    );
    handleAndRegisterEvent(event);
  }

  public void addProductVariationPieces(ProductId product, VariationId variation, ProductVariationPieces pieces) {
    if (pieces.isNone()) {
      return;
    }
    var event = new ProductVariationPiecesAdded(
        nextDomainEventHeader(),
        product,
        variation,
        pieces
    );
    handleAndRegisterEvent(event);
  }

  public void subtractProductVariationPieces(
      ProductId product, VariationId variation, ProductVariationPieces pieces
  ) {
    if (pieces.isNone()) {
      return;
    }
    var variationPieces = Optional.ofNullable(this.products.get(product))
        .map(variations -> variations.get(variation))
        .orElseGet(ProductVariationPieces::none);
    if (variationPieces.isLowerThan(pieces)) {
      throw InsufficientProductVariationPiecesException.insufficientPiecesForRemoval(
          product,
          variation,
          variationPieces,
          pieces
      );
    }
    var event = new ProductVariationPiecesRemoved(
        nextDomainEventHeader(),
        product,
        variation,
        pieces
    );
    handleAndRegisterEvent(event);
  }

  @Override
  protected void handle(DomainEvent event) {
    if (event instanceof StoreCreated created) {
      handle(created);
    } else if (event instanceof StoreAddressChanged addressChanged) {
      handle(addressChanged);
    } else if (event instanceof ProductVariationPiecesAdded productVariationPiecesAdded) {
      handle(productVariationPiecesAdded);
    } else if (event instanceof ProductVariationPiecesRemoved productVariationPiecesRemoved) {
      handle(productVariationPiecesRemoved);
    }
  }

  private void handle(StoreCreated event) {
    this.id = event.header().aggregateId();
    this.address = event.address();
  }

  private void handle(StoreAddressChanged event) {
    this.address = event.address();
  }

  private void handle(ProductVariationPiecesAdded event) {
    this.products.compute(event.product(), (product, variations) -> {
      if (isNull(variations)) {
        var newVariations = new HashMap<VariationId, ProductVariationPieces>();
        newVariations.put(event.variation(), event.pieces());
        return newVariations;
      } else {
        variations.compute(event.variation(), (variation, pieces) -> {
          if (isNull(pieces)) {
            return event.pieces();
          } else {
            return pieces.add(event.pieces());
          }
        });
        return variations;
      }
    });
  }

  private void handle(ProductVariationPiecesRemoved event) {
    this.products.get(event.product())
        .computeIfPresent(event.variation(), (variation, pieces) -> pieces.subtract(event.pieces()));
  }

  static StoreAggregate restore(
      Version version,
      EventId latestEvent,
      StoreId id,
      Address address,
      Map<ProductId, Map<VariationId, ProductVariationPieces>> products
  ) {
    var store = new StoreAggregate(version, latestEvent);
    store.id = id;
    store.address = address;
    store.products.putAll(products);
    return store;
  }
}
