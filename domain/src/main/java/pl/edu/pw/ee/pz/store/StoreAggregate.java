package pl.edu.pw.ee.pz.store;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PACKAGE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRoot;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateType;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;
import pl.edu.pw.ee.pz.sharedkernel.model.Pieces;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationPieces;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationPieces.VariationPieces;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreCode;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;
import pl.edu.pw.ee.pz.store.error.InsufficientProductVariationPiecesException;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesAdded;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesRemoved;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesUpdated;
import pl.edu.pw.ee.pz.store.event.StoreAddressChanged;
import pl.edu.pw.ee.pz.store.event.StoreCreated;

@Accessors(fluent = true)
public class StoreAggregate extends AggregateRoot<StoreId> {

  private static final AggregateType AGGREGATE_TYPE = new AggregateType("store");
  @Getter(PACKAGE)
  private StoreCode code;
  @Getter(PACKAGE)
  private Address address;
  @Getter(PACKAGE)
  private final Map<ProductId, Map<ProductVariationId, Pieces>> products = new HashMap<>();

  StoreAggregate(Version version, EventId latestEvent) {
    super(AGGREGATE_TYPE, version, latestEvent);
  }

  public StoreAggregate(StoreId id, StoreCode code, Address address) {
    this(Version.initial(), EventId.initial());
    var eventHeader = nextDomainEventHeader(id);
    var created = new StoreCreated(eventHeader, code, address);
    handleAndRegisterEvent(created);
  }

  public void changeAddress(Address address) {
    var event = new StoreAddressChanged(
        nextDomainEventHeader(),
        address
    );
    handleAndRegisterEvent(event);
  }

  public void setProductVariationPieces(ProductVariationPieces productVariationPieces) {
    var event = new ProductVariationPiecesUpdated(
        nextDomainEventHeader(),
        productVariationPieces
    );
    handleAndRegisterEvent(event);
  }

  public void addProductVariationPieces(
      ProductId product, ProductVariationId variation, Pieces pieces
  ) {
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
      ProductId product, ProductVariationId variation, Pieces pieces
  ) {
    if (pieces.isNone()) {
      return;
    }
    var variationPieces = Optional.ofNullable(this.products.get(product))
        .map(variations -> variations.get(variation))
        .orElseGet(Pieces::none);
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
  protected void handle(DomainEvent<StoreId> event) {
    if (event instanceof StoreCreated created) {
      handle(created);
    } else if (event instanceof StoreAddressChanged addressChanged) {
      handle(addressChanged);
    } else if (event instanceof ProductVariationPiecesAdded productVariationPiecesAdded) {
      handle(productVariationPiecesAdded);
    } else if (event instanceof ProductVariationPiecesRemoved productVariationPiecesRemoved) {
      handle(productVariationPiecesRemoved);
    } else if (event instanceof ProductVariationPiecesUpdated productVariationPiecesUpdated) {
      handle(productVariationPiecesUpdated);
    }
  }

  private void handle(StoreCreated event) {
    this.id = event.header().aggregateId();
    this.code = event.code();
    this.address = event.address();
    registerOutEvent(event);
  }

  private void handle(StoreAddressChanged event) {
    this.address = event.address();
  }

  private void handle(ProductVariationPiecesAdded event) {
    this.products.compute(event.product(), (product, variations) -> {
      if (isNull(variations)) {
        var newVariations = new HashMap<ProductVariationId, Pieces>();
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

  private void handle(ProductVariationPiecesUpdated event) {
    var product = event.productVariationPieces().product();
    var variationPieces = event.productVariationPieces().variationPieces().stream()
        .collect(Collectors.toMap(VariationPieces::variation, VariationPieces::pieces));
    this.products.put(product, variationPieces);
    registerOutEvent(event);
  }

  static StoreAggregate restore(
      Version version,
      EventId latestEvent,
      StoreId id,
      Address address,
      Map<ProductId, Map<ProductVariationId, Pieces>> products
  ) {
    var store = new StoreAggregate(version, latestEvent);
    store.id = id;
    store.address = address;
    store.products.putAll(products);
    return store;
  }

  public static AggregateType aggregateType() {
    return AGGREGATE_TYPE;
  }
}
