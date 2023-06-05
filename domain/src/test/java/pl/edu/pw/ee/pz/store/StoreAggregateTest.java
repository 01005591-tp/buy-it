package pl.edu.pw.ee.pz.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
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
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;
import pl.edu.pw.ee.pz.sharedkernel.model.Timestamp;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;
import pl.edu.pw.ee.pz.store.error.InsufficientProductVariationPiecesException;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesAdded;
import pl.edu.pw.ee.pz.store.event.ProductVariationPiecesRemoved;
import pl.edu.pw.ee.pz.store.event.StoreAddressChanged;
import pl.edu.pw.ee.pz.store.event.StoreCreated;

class StoreAggregateTest {

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

    // when
    var store = new StoreAggregate(
        storeId,
        address
    );

    // then
    assertThat(store.id()).isEqualTo(storeId);
    assertThat(store.address()).isEqualTo(address);
    assertThat(store.products()).isEmpty();
  }

  @Test
  void should_change_address() {
    // given
    var store = newStore();
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

    // when
    store.changeAddress(address);

    // then
    assertThat(store.address()).isEqualTo(address);
    assertThat(store.products()).isEmpty();
  }

  @Test
  void should_add_product_variation_pieces() {
    // given
    var store = newStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var variationId = new ProductVariationId(UUID.randomUUID());
    var pieces = ProductVariationPieces.of(3L);

    // when
    store.addProductVariationPieces(productId, variationId, pieces);

    // then
    assertThat(store.products())
        .hasSize(1)
        .anySatisfy((product, variations) -> {
          assertThat(product).isEqualTo(productId);
          assertThat(variations)
              .hasSize(1)
              .anySatisfy((variation, variationPieces) -> {
                assertThat(variation).isEqualTo(variationId);
                assertThat(variationPieces).isEqualTo(pieces);
              });
        });
  }

  @Test
  void should_add_more_product_variation_pieces() {
    // given
    var store = newStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var variationId = new ProductVariationId(UUID.randomUUID());
    store.addProductVariationPieces(productId, variationId, ProductVariationPieces.of(3L));

    // when
    store.addProductVariationPieces(productId, variationId, ProductVariationPieces.of(2L));

    // then
    assertThat(store.products())
        .hasSize(1)
        .anySatisfy((product, variations) -> {
          assertThat(product).isEqualTo(productId);
          assertThat(variations)
              .hasSize(1)
              .anySatisfy((variation, variationPieces) -> {
                assertThat(variation).isEqualTo(variationId);
                assertThat(variationPieces.count()).isEqualTo(5L);
              });
        });
  }

  @Test
  void should_remove_product_variation_pieces() {
    // given
    var store = newStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var variationId = new ProductVariationId(UUID.randomUUID());
    store.addProductVariationPieces(productId, variationId, ProductVariationPieces.of(3L));

    // when
    store.subtractProductVariationPieces(productId, variationId, ProductVariationPieces.of(3L));

    // then
    assertThat(store.products())
        .hasSize(1)
        .anySatisfy((product, variations) -> {
          assertThat(product).isEqualTo(productId);
          assertThat(variations)
              .hasSize(1)
              .anySatisfy((variation, pieces) -> {
                assertThat(variation).isEqualTo(variationId);
                assertThat(pieces.isNone()).isTrue();
              });
        });
  }

  @Test
  void should_fail_removing_pieces_when_insufficient_amount_in_store() {
    // given
    var store = newStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var variationId = new ProductVariationId(UUID.randomUUID());
    store.addProductVariationPieces(productId, variationId, ProductVariationPieces.of(2L));

    // when
    var throwableAssert = assertThatCode(
        () -> store.subtractProductVariationPieces(productId, variationId, ProductVariationPieces.of(3L))
    );

    // then
    throwableAssert
        .isInstanceOf(InsufficientProductVariationPiecesException.class)
        .hasMessage("Product %s variation %s available pieces is %d. Tried to remove %d pieces".formatted(
            productId.value(),
            variationId.value(),
            2,
            3
        ));
  }

  @Test
  void should_fail_removing_pieces_when_no_pieces_in_store() {
    // given
    var store = newStore();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var variationId = new ProductVariationId(UUID.randomUUID());

    // when
    var throwableAssert = assertThatCode(
        () -> store.subtractProductVariationPieces(productId, variationId, ProductVariationPieces.of(3L))
    );

    // then
    throwableAssert
        .isInstanceOf(InsufficientProductVariationPiecesException.class)
        .hasMessage("Product %s variation %s available pieces is %d. Tried to remove %d pieces".formatted(
            productId.value(),
            variationId.value(),
            0,
            3
        ));
  }

  @Test
  void should_restore_from_events() {
    // given
    var storeCreated = storeCreated();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var productVariationPiecesAdded = new ProductVariationPiecesAdded(
        newDomainEventHeader(storeCreated.header().aggregateId()),
        productId,
        new ProductVariationId(UUID.randomUUID()),
        ProductVariationPieces.of(5L)
    );
    // and
    var productVariationPiecesRemoved = new ProductVariationPiecesRemoved(
        newDomainEventHeader(storeCreated.header().aggregateId()),
        productId,
        productVariationPiecesAdded.variation(),
        ProductVariationPieces.of(3L)
    );
    // and
    var addressChanged = new StoreAddressChanged(
        newDomainEventHeader(storeCreated.header().aggregateId()),
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
    // and
    var events = List.<DomainEvent<StoreId>>of(
        storeCreated,
        productVariationPiecesAdded,
        productVariationPiecesRemoved,
        addressChanged
    );

    // when
    var store = StoreAggregate.restore(events, Version.initial(), StoreAggregate::new);

    // then
    assertThat(store.id()).isEqualTo(storeCreated.header().aggregateId());
    assertThat(store.address()).isEqualTo(addressChanged.address());
    assertThat(store.products())
        .hasSize(1)
        .allSatisfy((product, variations) ->
            assertThat(variations)
                .hasSize(1)
                .allSatisfy((variation, pieces) -> assertThat(pieces.count()).isEqualTo(2L))
        );
  }

  @Test
  void should_restore_from_snapshot() {
    // given
    var storeCreated = storeCreated();
    // and
    var productId = new ProductId(UUID.randomUUID());
    var productVariationPiecesAdded = new ProductVariationPiecesAdded(
        newDomainEventHeader(storeCreated.header().aggregateId()),
        productId,
        new ProductVariationId(UUID.randomUUID()),
        ProductVariationPieces.of(5L)
    );
    // and
    var productVariationPiecesRemoved = new ProductVariationPiecesRemoved(
        newDomainEventHeader(storeCreated.header().aggregateId()),
        productVariationPiecesAdded.product(),
        productVariationPiecesAdded.variation(),
        ProductVariationPieces.of(3L)
    );
    // and
    var addressChanged = new StoreAddressChanged(
        newDomainEventHeader(storeCreated.header().aggregateId()),
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
    // and
    var events = List.<DomainEvent<StoreId>>of(
        storeCreated,
        productVariationPiecesAdded,
        productVariationPiecesRemoved,
        addressChanged
    );
    var version = Version.specified(1L);
    var storeFromEvents = StoreAggregate.restore(events, Version.initial(), StoreAggregate::new);

    // when
    var store = StoreAggregate.restore(
        version, new EventId(1L), storeFromEvents.id(), storeFromEvents.address(), storeFromEvents.products());

    // then
    assertThat(store.id()).isEqualTo(storeFromEvents.id());
    assertThat(store.address()).isEqualTo(storeFromEvents.address());
    assertThat(store.products())
        .hasSameSizeAs(storeFromEvents.products())
        .allSatisfy((product, variations) -> {
          var variationsFromEvents = storeFromEvents.products().get(product);
          assertThat(variations)
              .hasSameSizeAs(variationsFromEvents)
              .allSatisfy((variation, pieces) -> {
                var piecesFromEvent = variationsFromEvents.get(variation);
                assertThat(pieces).isEqualTo(piecesFromEvent);
              });
        });
  }

  private StoreAggregate newStore() {
    return new StoreAggregate(
        new StoreId(UUID.randomUUID()),
        new Address(
            new Street(
                new StreetName("Street"),
                new HouseNo("23A/B 44")
            ),
            new City("City"),
            new ZipCode("01-234"),
            Country.PL
        )
    );
  }

  private StoreCreated storeCreated() {
    var storeId = new StoreId(UUID.randomUUID());
    return new StoreCreated(
        newDomainEventHeader(storeId),
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

  private static final AtomicLong LONG_SEQ = new AtomicLong(0L);

  private <ID extends AggregateId> DomainEventHeader<ID> newDomainEventHeader(ID aggregateId) {
    return new DomainEventHeader<>(
        new EventId(LONG_SEQ.incrementAndGet()),
        aggregateId,
        new Timestamp(Instant.now())
    );
  }
}