package pl.edu.pw.ee.pz.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.DomainEventHeader;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.City;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.HouseNo;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.Street;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.StreetName;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.ZipCode;
import pl.edu.pw.ee.pz.sharedkernel.model.Country;
import pl.edu.pw.ee.pz.sharedkernel.model.CountryCode;
import pl.edu.pw.ee.pz.sharedkernel.model.Timestamp;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;
import pl.edu.pw.ee.pz.store.Product.ProductId;
import pl.edu.pw.ee.pz.store.ProductVariation.VariationId;
import pl.edu.pw.ee.pz.store.Store.StoreId;
import pl.edu.pw.ee.pz.store.VariationAttribute.AttributeType;
import pl.edu.pw.ee.pz.store.VariationAttribute.AttributeValue;
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

class StoreTest {

  @Test
  void should_create_store() {
    // given
    var storeId = new StoreId(UUID.randomUUID());
    var address = new Address(
        new Street(
            new StreetName("Street"),
            new HouseNo("23A/B 44")
        ),
        new City("City"),
        new ZipCode("01-234"),
        Country.PL
    );
    var timestamp = new Timestamp(Instant.now());

    // when
    var store = new Store(
        storeId,
        address,
        timestamp
    );

    // then
    assertThat(store.id()).isEqualTo(storeId);
    assertThat(store.address()).isEqualTo(address);
    assertThat(store.products()).isEmpty();
  }

  @Test
  void should_change_address() {
    // given
    var store = newEmptyStore();
    // and
    var address = new Address(
        new Street(
            new StreetName("NewStreet"),
            new HouseNo("NewHouseNo")
        ),
        new City("NewCity"),
        new ZipCode("NewZipCode"),
        new Country(CountryCode.of("CZ"))
    );
    var event = new StoreAddressChanged(
        newDomainEventHeader(),
        address
    );

    // when
    store.dispatchAndHandle(event);

    // then
    assertThat(store.id()).isEqualTo(store.id());
    assertThat(store.address()).isEqualTo(address);
    assertThat(store.products()).isEmpty();
  }

  @Test
  void should_add_product() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var productAdded = new ProductAdded(
        newDomainEventHeader(),
        productId
    );

    // when
    store.dispatchAndHandle(productAdded);

    // then
    assertThat(store.products())
        .hasSize(1)
        .anySatisfy((product, variations) -> {
          assertThat(product).isEqualTo(product);
          assertThat(variations).isEmpty();
        });
  }

  @Test
  void should_fail_adding_already_existing_product() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var productAdded = new ProductAdded(
        newDomainEventHeader(),
        productId
    );
    store.dispatchAndHandle(productAdded);

    // when
    var throwableAssert = assertThatCode(() -> store.dispatchAndHandle(productAdded));

    // then
    throwableAssert
        .isInstanceOf(ProductAlreadyExistsException.class)
        .hasMessage("Product " + productId.value().toString() + " already exists");
  }

  @Test
  void should_remove_product() {
    // given

  }

  @Test
  void should_add_product_variation() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    // and
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    // and
    var productVariation = productVariation(productId);
    var event = new ProductVariationAdded(
        newDomainEventHeader(),
        productVariation
    );

    // when
    store.dispatchAndHandle(event);

    // then
    assertThat(store.products())
        .hasSize(1)
        .anySatisfy((product, variations) -> {
          assertThat(product).isEqualTo(productId);
          assertThat(variations)
              .hasSize(1)
              .anySatisfy((variationId, variationAvailability) -> {
                assertThat(variationId).isEqualTo(productVariation.id());
                assertThat(variationAvailability.variation()).isEqualTo(productVariation);
                assertThat(variationAvailability.pieces().isNone()).isTrue();
              });
        });
  }

  @Test
  void should_add_another_product_variation() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    // and
    var productVariation = productVariation(productId);
    store.dispatchAndHandle(new ProductVariationAdded(
        newDomainEventHeader(),
        productVariation
    ));
    // and
    var anotherProductVariation = productVariation(productId);
    var event = new ProductVariationAdded(
        newDomainEventHeader(),
        anotherProductVariation
    );

    // when
    store.dispatchAndHandle(event);

    // then
    assertThat(store.products())
        .hasSize(1)
        .anySatisfy((product, variations) -> {
          assertThat(product).isEqualTo(productId);
          assertThat(variations)
              .hasSize(2)
              .anySatisfy((variationId, variationAvailability) ->
                  assertThat(variationId).isEqualTo(productVariation.id())
              )
              .anySatisfy((variationId, variationAvailability) -> {
                assertThat(variationId).isEqualTo(anotherProductVariation.id());
                assertThat(variationAvailability.variation()).isEqualTo(anotherProductVariation);
                assertThat(variationAvailability.pieces().isNone()).isTrue();
              });
        });
  }

  @Test
  void should_fail_adding_already_existing_variation() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    // and
    var productVariation = productVariation(productId);
    store.dispatchAndHandle(new ProductVariationAdded(
        newDomainEventHeader(),
        productVariation
    ));
    // and
    var event = new ProductVariationAdded(
        newDomainEventHeader(),
        productVariation
    );

    // when
    var throwableAssert = assertThatCode(() -> store.dispatchAndHandle(event));

    // then
    throwableAssert
        .isInstanceOf(ProductVariationAlreadyExistsException.class)
        .hasMessage("Product " + productVariation.id().value().toString() + " already exists");
  }

  @Test
  void should_remove_product_variation() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    // and
    var productVariation = productVariation(productId);
    store.dispatchAndHandle(new ProductVariationAdded(
        newDomainEventHeader(),
        productVariation
    ));
    store.dispatchAndHandle(new ProductVariationAdded(
        newDomainEventHeader(),
        productVariation(productId)
    ));
    // and
    var event = new ProductVariationRemoved(
        newDomainEventHeader(),
        productId,
        productVariation.id()
    );

    // when
    store.dispatchAndHandle(event);

    // then
    assertThat(store.products())
        .hasSize(1)
        .anySatisfy((product, variations) -> {
          assertThat(product).isEqualTo(productId);
          assertThat(variations)
              .hasSize(1)
              .noneSatisfy((variationId, availability) ->
                  assertThat(variationId).isEqualTo(productVariation.id())
              );
        });
  }

  @Test
  void should_fail_remove_product_variation_when_product_missing() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var productVariation = productVariation(productId);
    var event = new ProductVariationRemoved(
        newDomainEventHeader(),
        productId,
        productVariation.id()
    );

    // when
    var throwableAssert = assertThatCode(() -> store.dispatchAndHandle(event));

    // then
    throwableAssert
        .isInstanceOf(ProductNotInStoreException.class)
        .hasMessage("Product " + productId.value().toString() + " not in store");
  }

  @Test
  void should_fail_remove_product_variation_when_variation_missing() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    var productVariation = productVariation(productId);
    var event = new ProductVariationRemoved(
        newDomainEventHeader(),
        productId,
        productVariation.id()
    );

    // when
    var throwableAssert = assertThatCode(() -> store.dispatchAndHandle(event));

    // then
    throwableAssert
        .isInstanceOf(ProductVariationUndefinedException.class)
        .hasMessage("Variation " + productVariation.id().value().toString() + " not defined for product "
            + productId.value().toString());
  }

  @Test
  void should_add_product_variation_pieces() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    // and
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    // and
    var productVariation = productVariation(productId);
    store.dispatchAndHandle(new ProductVariationAdded(
        newDomainEventHeader(),
        productVariation
    ));
    // and
    var pieces = ProductVariationPieces.of(3L);
    var event = new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        pieces
    );

    // when
    store.dispatchAndHandle(event);

    // then
    assertThat(store.products())
        .hasSize(1)
        .anySatisfy((product, variations) -> {
          assertThat(product).isEqualTo(productId);
          assertThat(variations)
              .hasSize(1)
              .anySatisfy((variationId, availability) -> {
                assertThat(variationId).isEqualTo(productVariation.id());
                assertThat(availability.pieces()).isEqualTo(pieces);
              });
        });
  }

  @Test
  void should_add_product_variation_pieces_when_some_already_exist() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    // and
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    // and
    var productVariation = productVariation(productId);
    store.dispatchAndHandle(new ProductVariationAdded(
        newDomainEventHeader(),
        productVariation
    ));
    // and
    store.dispatchAndHandle(new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        ProductVariationPieces.of(5L)
    ));
    var pieces = ProductVariationPieces.of(3L);
    var event = new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        pieces
    );

    // when
    store.dispatchAndHandle(event);

    // then
    assertThat(store.products())
        .hasSize(1)
        .anySatisfy((product, variations) -> {
          assertThat(product).isEqualTo(productId);
          assertThat(variations)
              .hasSize(1)
              .anySatisfy((variationId, availability) -> {
                assertThat(variationId).isEqualTo(productVariation.id());
                assertThat(availability.pieces().count()).isEqualTo(8L);
              });
        });
  }

  @Test
  void should_fail_adding_product_variation_pieces_when_product_missing() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var productVariation = productVariation(productId);
    // and
    var pieces = ProductVariationPieces.of(3L);
    var event = new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        pieces
    );

    // when
    var throwableAssert = assertThatCode(() -> store.dispatchAndHandle(event));

    // then
    throwableAssert
        .isInstanceOf(ProductNotInStoreException.class)
        .hasMessage("Product " + productId.value().toString() + " not in store");
  }

  @Test
  void should_fail_adding_product_variation_pieces_when_product_variation_missing() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    var productVariation = productVariation(productId);
    // and
    var pieces = ProductVariationPieces.of(3L);
    var event = new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        pieces
    );

    // when
    var throwableAssert = assertThatCode(() -> store.dispatchAndHandle(event));

    // then
    throwableAssert
        .isInstanceOf(ProductVariationUndefinedException.class)
        .hasMessage("Variation " + productVariation.id().value().toString() + " not defined for product "
            + productId.value().toString());
  }

  @Test
  void should_remove_product_variation_pieces() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    // and
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    // and
    var productVariation = productVariation(productId);
    store.dispatchAndHandle(new ProductVariationAdded(
        newDomainEventHeader(),
        productVariation
    ));
    // and
    store.dispatchAndHandle(new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        ProductVariationPieces.of(3L)
    ));
    var pieces = ProductVariationPieces.of(3L);
    var event = new ProductVariationPiecesRemoved(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        pieces
    );

    // when
    store.dispatchAndHandle(event);

    // then
    assertThat(store.products())
        .hasSize(1)
        .anySatisfy((product, variations) -> {
          assertThat(product).isEqualTo(productId);
          assertThat(variations)
              .hasSize(1)
              .anySatisfy((variationId, availability) -> {
                assertThat(variationId).isEqualTo(productVariation.id());
                assertThat(availability.pieces().isNone()).isTrue();
              });
        });
  }

  @Test
  void should_fail_removing_product_variation_pieces_when_insufficient_amount_in_store() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    // and
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    // and
    var productVariation = productVariation(productId);
    store.dispatchAndHandle(new ProductVariationAdded(
        newDomainEventHeader(),
        productVariation
    ));
    // and
    store.dispatchAndHandle(new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        ProductVariationPieces.of(2L)
    ));
    var pieces = ProductVariationPieces.of(3L);
    var event = new ProductVariationPiecesRemoved(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        pieces
    );

    // when
    var throwableAssert = assertThatCode(() -> store.dispatchAndHandle(event));

    // then
    throwableAssert
        .isInstanceOf(InsufficientProductVariationPiecesException.class)
        .hasMessage("Product " + productId.value().toString() + " variation " + productVariation.id().value().toString()
            + " available pieces is " + 2 + ". Tried to remove " + 3 + " pieces");
  }

  @Test
  void should_fail_removing_product_variation_pieces_when_product_missing() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    // and
    var productVariation = productVariation(productId);
    // and
    var pieces = ProductVariationPieces.of(3L);
    var event = new ProductVariationPiecesRemoved(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        pieces
    );

    // when
    var throwableAssert = assertThatCode(() -> store.dispatchAndHandle(event));

    // then
    throwableAssert
        .isInstanceOf(ProductNotInStoreException.class)
        .hasMessage("Product " + productId.value().toString() + " not in store");
  }

  @Test
  void should_fail_removing_product_variation_pieces_when_product_variation_missing() {
    // given
    var store = newEmptyStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    // and
    store.dispatchAndHandle(new ProductAdded(
        newDomainEventHeader(),
        productId
    ));
    // and
    var productVariation = productVariation(productId);
    var pieces = ProductVariationPieces.of(3L);
    var event = new ProductVariationPiecesRemoved(
        newDomainEventHeader(),
        productId,
        productVariation.id(),
        pieces
    );

    // when
    var throwableAssert = assertThatCode(() -> store.dispatchAndHandle(event));

    // then
    throwableAssert
        .isInstanceOf(ProductVariationUndefinedException.class)
        .hasMessage("Variation " + productVariation.id().value().toString() + " not defined for product "
            + productId.value().toString());
  }

  @Test
  void should_restore_store() {
    // given
    var storeCreated = storeCreated();
    // and
    var addressChanged = addressChanged();
    // and
    var product1Added = productAdded();
    var product2Added = productAdded();
    // and
    var product1Variation1Added = product1Variation1Added(product1Added.productId());
    var product1Variation1PiecesAdded = new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        product1Added.productId(),
        product1Variation1Added.productVariation().id(),
        ProductVariationPieces.of(5L)
    );
    // and
    var product3Added = productAdded();
    // and
    var product2Removed = productRemoved(product2Added.productId());
    // and
    var product1Variation2Added = product1Variation2Added(product1Added.productId());
    var product1Variation2PiecesAdded = new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        product1Added.productId(),
        product1Variation2Added.productVariation().id(),
        ProductVariationPieces.of(3L)
    );
    // and
    var product1Variation2PiecesRemoved = new ProductVariationPiecesRemoved(
        newDomainEventHeader(),
        product1Added.productId(),
        product1Variation2Added.productVariation().id(),
        ProductVariationPieces.of(2L)
    );
    // and
    var product1Variation1Removed = new ProductVariationRemoved(
        newDomainEventHeader(),
        product1Added.productId(),
        product1Variation1Added.productVariation().id()
    );
    // and
    List<DomainEvent> events = List.of(
        storeCreated,
        addressChanged,
        product1Added,
        product2Added,
        product1Variation1Added,
        product1Variation1PiecesAdded,
        product3Added,
        product2Removed,
        product1Variation2Added,
        product1Variation2PiecesAdded,
        product1Variation2PiecesRemoved,
        product1Variation1Removed
    );

    // when
    var store = Store.restore(events, new Version(1L));

    // then
    assertThat(store.id()).isEqualTo(storeCreated.id());
    assertThat(store.address()).isEqualTo(addressChanged.address());
    assertThat(store.products())
        .hasSize(2)
        .anySatisfy((productId, variations) -> {
          assertThat(productId).isEqualTo(product1Added.productId());
          assertThat(variations).hasSize(1);
          var variation = variations.get(product1Variation2Added.productVariation().id());
          assertThat(variation.variation().id()).isEqualTo(product1Variation2Added.productVariation().id());
          assertThat(variation.variation().product()).isEqualTo(product1Added.productId());
          assertThat(variation.variation().attributes())
              .hasSize(3)
              .anySatisfy(variationAttribute -> {
                assertThat(variationAttribute.type().value()).isEqualTo("SIZE");
                assertThat(variationAttribute.value().value()).isEqualTo("36");
              })
              .anySatisfy(variationAttribute -> {
                assertThat(variationAttribute.type().value()).isEqualTo("COLOR1");
                assertThat(variationAttribute.value().value()).isEqualTo("GREY");
              })
              .anySatisfy(variationAttribute -> {
                assertThat(variationAttribute.type().value()).isEqualTo("COLOR2");
                assertThat(variationAttribute.value().value()).isEqualTo("BLACK");
              });
        })
        .anySatisfy((productId, variations) -> {
          assertThat(productId).isEqualTo(product3Added.productId());
          assertThat(variations).isEmpty();
        });
  }

  @Test
  void should_restore_from_snapshot() {
// given
    var storeCreated = storeCreated();
    // and
    var addressChanged = addressChanged();
    // and
    var product1Added = productAdded();
    var product2Added = productAdded();
    // and
    var product1Variation1Added = product1Variation1Added(product1Added.productId());
    var product1Variation1PiecesAdded = new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        product1Added.productId(),
        product1Variation1Added.productVariation().id(),
        ProductVariationPieces.of(5L)
    );
    // and
    var product3Added = productAdded();
    // and
    var product2Removed = productRemoved(product2Added.productId());
    // and
    var product1Variation2Added = product1Variation2Added(product1Added.productId());
    var product1Variation2PiecesAdded = new ProductVariationPiecesAdded(
        newDomainEventHeader(),
        product1Added.productId(),
        product1Variation2Added.productVariation().id(),
        ProductVariationPieces.of(3L)
    );
    // and
    var product1Variation2PiecesRemoved = new ProductVariationPiecesRemoved(
        newDomainEventHeader(),
        product1Added.productId(),
        product1Variation2Added.productVariation().id(),
        ProductVariationPieces.of(2L)
    );
    // and
    var product1Variation1Removed = new ProductVariationRemoved(
        newDomainEventHeader(),
        product1Added.productId(),
        product1Variation1Added.productVariation().id()
    );
    // and
    List<DomainEvent> events = List.of(
        storeCreated,
        addressChanged,
        product1Added,
        product2Added,
        product1Variation1Added,
        product1Variation1PiecesAdded,
        product3Added,
        product2Removed,
        product1Variation2Added,
        product1Variation2PiecesAdded,
        product1Variation2PiecesRemoved,
        product1Variation1Removed
    );
    // and
    var eventBasedStore = Store.restore(events, new Version(1L));

    // when
    var store = Store.restore(
        new Version(2L),
        new EventId(32L),
        eventBasedStore.id(),
        eventBasedStore.address(),
        eventBasedStore.products()
    );

    // then
    assertThat(store.id()).isEqualTo(storeCreated.id());
    assertThat(store.address()).isEqualTo(addressChanged.address());
    assertThat(store.products())
        .hasSize(2)
        .anySatisfy((productId, variations) -> {
          assertThat(productId).isEqualTo(product1Added.productId());
          assertThat(variations).hasSize(1);
          var variation = variations.get(product1Variation2Added.productVariation().id());
          assertThat(variation.variation().id()).isEqualTo(product1Variation2Added.productVariation().id());
          assertThat(variation.variation().product()).isEqualTo(product1Added.productId());
          assertThat(variation.variation().attributes())
              .hasSize(3)
              .anySatisfy(variationAttribute -> {
                assertThat(variationAttribute.type().value()).isEqualTo("SIZE");
                assertThat(variationAttribute.value().value()).isEqualTo("36");
              })
              .anySatisfy(variationAttribute -> {
                assertThat(variationAttribute.type().value()).isEqualTo("COLOR1");
                assertThat(variationAttribute.value().value()).isEqualTo("GREY");
              })
              .anySatisfy(variationAttribute -> {
                assertThat(variationAttribute.type().value()).isEqualTo("COLOR2");
                assertThat(variationAttribute.value().value()).isEqualTo("BLACK");
              });
        })
        .anySatisfy((productId, variations) -> {
          assertThat(productId).isEqualTo(product3Added.productId());
          assertThat(variations).isEmpty();
        });
  }

  private ProductRemoved productRemoved(ProductId productId) {
    return new ProductRemoved(
        newDomainEventHeader(),
        productId
    );
  }

  private ProductVariationAdded product1Variation1Added(ProductId productId) {
    return new ProductVariationAdded(
        newDomainEventHeader(),
        new ProductVariation(
            new VariationId(UUID.randomUUID()),
            productId,
            Set.of(
                new VariationAttribute(
                    new AttributeType("SIZE"),
                    new AttributeValue("36")
                ),
                new VariationAttribute(
                    new AttributeType("COLOR1"),
                    new AttributeValue("BROWN")
                ),
                new VariationAttribute(
                    new AttributeType("COLOR2"),
                    new AttributeValue("WHITE")
                )
            )
        )
    );
  }

  private ProductVariationAdded product1Variation2Added(ProductId productId) {
    return new ProductVariationAdded(
        newDomainEventHeader(),
        new ProductVariation(
            new VariationId(UUID.randomUUID()),
            productId,
            Set.of(
                new VariationAttribute(
                    new AttributeType("SIZE"),
                    new AttributeValue("36")
                ),
                new VariationAttribute(
                    new AttributeType("COLOR1"),
                    new AttributeValue("GREY")
                ),
                new VariationAttribute(
                    new AttributeType("COLOR2"),
                    new AttributeValue("BLACK")
                )
            )
        )
    );
  }

  private ProductAdded productAdded() {
    return new ProductAdded(
        newDomainEventHeader(),
        new ProductId(UUID.randomUUID())
    );
  }

  private StoreAddressChanged addressChanged() {
    return new StoreAddressChanged(
        newDomainEventHeader(),
        new Address(
            new Street(
                new StreetName("NewStreet"),
                new HouseNo("NewHouseNo")
            ),
            new City("NewCity"),
            new ZipCode("NewZipCode"),
            new Country(CountryCode.of("CZ"))
        )
    );
  }

  private StoreCreated storeCreated() {
    return new StoreCreated(
        newDomainEventHeader(),
        new StoreId(UUID.randomUUID()),
        new Address(
            new Street(
                new StreetName("Street"),
                new HouseNo("HouseNo")
            ),
            new City("City"),
            new ZipCode("ZipCode"),
            new Country(CountryCode.of("PL"))
        )
    );
  }

  private Store newEmptyStore() {
    return new Store(
        new StoreId(UUID.randomUUID()),
        new Address(
            new Street(
                new StreetName("Street"),
                new HouseNo("23A/B 44")
            ),
            new City("City"),
            new ZipCode("01-234"),
            Country.PL
        ),
        new Timestamp(Instant.now())
    );
  }

  private ProductVariation productVariation(ProductId productId) {
    return new ProductVariation(
        new VariationId(UUID.randomUUID()),
        productId,
        Set.of()
    );
  }

  private static final AtomicLong LONG_SEQ = new AtomicLong(0L);

  private DomainEventHeader newDomainEventHeader() {
    return new DomainEventHeader(
        new EventId(LONG_SEQ.incrementAndGet()),
        new Timestamp(Instant.now())
    );
  }
}