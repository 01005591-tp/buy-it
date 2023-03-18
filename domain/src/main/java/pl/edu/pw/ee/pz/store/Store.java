package pl.edu.pw.ee.pz.store;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PACKAGE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRoot;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRootUtils;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.DomainEventHeader;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;
import pl.edu.pw.ee.pz.sharedkernel.model.Timestamp;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;
import pl.edu.pw.ee.pz.store.Product.ProductId;
import pl.edu.pw.ee.pz.store.ProductVariation.VariationId;
import pl.edu.pw.ee.pz.store.error.InsufficientProductVariationPiecesException;
import pl.edu.pw.ee.pz.store.error.ProductAlreadyExistsException;
import pl.edu.pw.ee.pz.store.error.ProductNotInStoreException;
import pl.edu.pw.ee.pz.store.error.ProductVariationAlreadyExistsException;
import pl.edu.pw.ee.pz.store.error.ProductVariationUndefinedException;
import pl.edu.pw.ee.pz.store.event.ProductAdded;
import pl.edu.pw.ee.pz.store.event.ProductRemoved;
import pl.edu.pw.ee.pz.store.event.ProductVariationAdded;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesAdded;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesRemoved;
import pl.edu.pw.ee.pz.store.event.ProductVariationRemoved;
import pl.edu.pw.ee.pz.store.event.StoreAddressChanged;
import pl.edu.pw.ee.pz.store.event.StoreCreated;

@Accessors(fluent = true)
public class Store extends AggregateRoot {

  @Getter(PACKAGE)
  private StoreId id;
  @Getter(PACKAGE)
  private Address address;
  @Getter(PACKAGE)
  private final Map<ProductId, Map<VariationId, ProductVariationAvailability>> products = new HashMap<>();

  private Store(Version version, EventId latestEvent) {
    this.version = version;
    this.latestEvent = latestEvent;
  }

  public Store(
      StoreId id,
      Address address,
      Timestamp timestamp
  ) {
    this.latestEvent = EventId.initial();
    var eventHeader = new DomainEventHeader(latestEvent.next(), timestamp);
    var created = new StoreCreated(eventHeader, id, address);
    handleAndRegisterEvent(created);
  }

  @Override
  protected void dispatchAndHandle(DomainEvent event) {
    if (event instanceof StoreCreated created) {
      handle(created);
    } else if (event instanceof StoreAddressChanged addressChanged) {
      handle(addressChanged);
    } else if (event instanceof ProductAdded productAdded) {
      handle(productAdded);
    } else if (event instanceof ProductRemoved productRemoved) {
      handle(productRemoved);
    } else if (event instanceof ProductVariationAdded productVariationAdded) {
      handle(productVariationAdded);
    } else if (event instanceof ProductVariationRemoved productVariationRemoved) {
      handle(productVariationRemoved);
    } else if (event instanceof ProductVariationPiecesAdded productVariationPiecesAdded) {
      handle(productVariationPiecesAdded);
    } else if (event instanceof ProductVariationPiecesRemoved productVariationPiecesRemoved) {
      handle(productVariationPiecesRemoved);
    }
  }

  private void handle(StoreCreated created) {
    this.id = created.id();
    this.address = created.address();
  }

  private void handle(StoreAddressChanged addressChanged) {
    this.address = addressChanged.address();
  }

  private void handle(ProductAdded productAdded) {
    var productId = productAdded.productId();
    if (this.products.containsKey(productId)) {
      throw ProductAlreadyExistsException.alreadyExists(productId);
    }
    this.products.put(productId, new HashMap<>());
  }

  private void handle(ProductRemoved productRemoved) {
    var productId = productRemoved.productId();
    var removed = this.products.remove(productId);
    if (isNull(removed)) {
      throw ProductNotInStoreException.notInStore(productId);
    }
  }

  private void handle(ProductVariationAdded productVariationAdded) {
    var productId = productVariationAdded.productVariation().product();
    assertProductInStore(productId);

    var variations = this.products.get(productId);
    if (variations.containsKey(productVariationAdded.productVariation().id())) {
      throw ProductVariationAlreadyExistsException.alreadyExists(productVariationAdded.productVariation().id());
    } else {
      var productVariation = ProductVariationAvailability.empty(productVariationAdded.productVariation());
      variations.put(productVariation.variation().id(), productVariation);
    }
  }

  private void handle(ProductVariationRemoved productVariationRemoved) {
    assertProductInStore(productVariationRemoved.product());

    var variations = this.products.get(productVariationRemoved.product());
    var removed = variations.remove(productVariationRemoved.variation());
    if (isNull(removed)) {
      throw ProductVariationUndefinedException.variationUndefined(
          productVariationRemoved.product(),
          productVariationRemoved.variation()
      );
    }
  }

  private void handle(ProductVariationPiecesAdded productVariationPiecesAdded) {
    var productId = productVariationPiecesAdded.product();
    assertProductInStore(productId);
    var variations = this.products.get(productVariationPiecesAdded.product());
    if (isNull(variations)) {
      throw ProductVariationUndefinedException.variationUndefined(productId, productVariationPiecesAdded.variation());
    }
    variations.compute(
        productVariationPiecesAdded.variation(),
        (variationId, availability) -> {
          assertVerificationDefinedForProduct(
              productVariationPiecesAdded.product(),
              productVariationPiecesAdded.variation(),
              availability
          );
          return availability.addPieces(productVariationPiecesAdded.pieces());
        }
    );
  }

  private void handle(ProductVariationPiecesRemoved productVariationPiecesRemoved) {
    assertProductInStore(productVariationPiecesRemoved.product());

    var variations = this.products.get(productVariationPiecesRemoved.product());
    variations.compute(
        productVariationPiecesRemoved.variation(),
        (variationId, availability) -> {
          assertVerificationDefinedForProduct(
              productVariationPiecesRemoved.product(),
              productVariationPiecesRemoved.variation(),
              availability
          );
          if (availability.pieces().isLowerThan(productVariationPiecesRemoved.pieces())) {
            throw InsufficientProductVariationPiecesException.insufficientPiecesForRemoval(
                productVariationPiecesRemoved,
                availability.pieces()
            );
          }
          return availability.removePieces(productVariationPiecesRemoved.pieces());
        }
    );
  }

  static Store restore(List<DomainEvent> inEvents, Version version) {
    return AggregateRootUtils.restore(inEvents, version, Store::new);
  }

  static Store restore(
      Version version,
      EventId latestEvent,
      StoreId id,
      Address address,
      Map<ProductId, Map<VariationId, ProductVariationAvailability>> products
  ) {
    var store = new Store(version, latestEvent);
    store.id = id;
    store.address = address;
    store.products.putAll(products);
    return store;
  }

  private void assertProductInStore(ProductId productId) {
    if (!this.products.containsKey(productId)) {
      throw ProductNotInStoreException.notInStore(productId);
    }
  }

  private void assertVerificationDefinedForProduct(
      ProductId productId,
      VariationId variationId,
      ProductVariationAvailability availability
  ) {
    if (isNull(availability)) {
      throw ProductVariationUndefinedException.variationUndefined(
          productId,
          variationId
      );
    }
  }

  public record StoreId(UUID id) {

  }
}
