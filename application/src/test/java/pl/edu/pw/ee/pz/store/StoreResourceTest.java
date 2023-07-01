package pl.edu.pw.ee.pz.store;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.sharedkernel.model.Pieces;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;
import pl.edu.pw.ee.pz.store.port.StoreAggregatePort;
import pl.edu.pw.ee.pz.util.CustomResourcesInitializer;

@QuarkusTest
@QuarkusTestResource(CustomResourcesInitializer.class)
class StoreResourceTest {

  @Inject
  StoreFixture storeFixture;
  @Inject
  StoreAggregatePort storeAggregatePort;
  @Inject
  StoreProjectionPort storeProjectionPort;

  @Test
  void should_create_store() {
    // when
    var storeId = storeFixture.createStore();

    // then
    var storeAggregate = storeAggregatePort.findById(storeId)
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(storeAggregate.id()).isEqualTo(storeId);
    assertThat(storeAggregate.code().value()).isEqualTo("STORE_1");
    assertThat(storeAggregate.products()).isEmpty();
    // and
    var store = findStoreProjectionById(storeId, Objects::nonNull);
    assertThat(store.id()).isEqualTo(storeId);
    assertThat(store.code().value()).isEqualTo("STORE_1");
    assertThat(store.products()).isEmpty();
  }

  @Test
  void should_update_products_available_pieces() {
    // given
    var storeId = storeFixture.createStore();

    // when
    storeFixture.updateStoreProductAvailability(storeId);

    // then
    var storeAggregate = storeAggregatePort.findById(storeId)
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(storeAggregate.id()).isEqualTo(storeId);
    assertThat(storeAggregate.code().value()).isEqualTo("STORE_1");
    assertStoreCreatedProducts(storeAggregate.products());
    // and
    var store = findStoreProjectionById(storeId, Objects::nonNull);
    assertThat(store.id()).isEqualTo(storeId);
    assertThat(store.code().value()).isEqualTo("STORE_1");
    assertStoreCreatedProducts(store.products());
  }

  private static void assertStoreCreatedProducts(
      Map<ProductId, Map<ProductVariationId, Pieces>> products
  ) {
    assertThat(products)
        .hasSize(2)
        .anySatisfy((product, variations) -> {
          assertThat(product).isNotNull();
          assertThat(variations)
              .hasSize(3)
              .anySatisfy((variationId, pieces) -> {
                assertThat(variationId).isNotNull();
                assertThat(pieces.value()).isEqualTo(3L);
              })
              .anySatisfy((variationId, pieces) -> {
                assertThat(variationId).isNotNull();
                assertThat(pieces.value()).isEqualTo(5L);
              })
              .anySatisfy((variationId, pieces) -> {
                assertThat(variationId).isNotNull();
                assertThat(pieces.value()).isEqualTo(7L);
              });
        })
        .anySatisfy((product, variations) -> {
          assertThat(product).isNotNull();
          assertThat(variations)
              .hasSize(2)
              .anySatisfy((variationId, pieces) -> {
                assertThat(variationId).isNotNull();
                assertThat(pieces.value()).isEqualTo(2L);
              })
              .anySatisfy((variationId, pieces) -> {
                assertThat(variationId).isNotNull();
                assertThat(pieces.value()).isEqualTo(4L);
              });
        });
  }

  private Store findStoreProjectionById(StoreId id, Predicate<Store> predicate) {
    return Awaitility.await()
        .ignoreExceptions()
        .until(
            () -> storeProjectionPort.findById(id)
                .await().atMost(Duration.ofSeconds(1L)),
            predicate
        );
  }
}