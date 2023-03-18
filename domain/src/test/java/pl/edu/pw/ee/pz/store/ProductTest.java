package pl.edu.pw.ee.pz.store;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.DomainEventHeader;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.Timestamp;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;
import pl.edu.pw.ee.pz.store.Product.ProductBrand;
import pl.edu.pw.ee.pz.store.Product.ProductId;
import pl.edu.pw.ee.pz.store.Product.ProductName;
import pl.edu.pw.ee.pz.store.event.ProductModified;

class ProductTest {

  @Test
  void should_create_new_product() {
    // given
    var productId = new ProductId(UUID.randomUUID());
    var productName = new ProductName("Product1");
    var productBrand = new ProductBrand("Product1_brand");

    // when
    var product = new Product(
        productId,
        productName,
        productBrand,
        new Timestamp(Instant.now())
    );

    // then
    assertThat(product.id()).isEqualTo(productId);
    assertThat(product.name()).isEqualTo(productName);
    assertThat(product.brand()).isEqualTo(productBrand);
  }

  @Test
  void should_modify_product() {
    // given
    var product = new Product(
        new ProductId(UUID.randomUUID()),
        new ProductName("Product1"),
        new ProductBrand("Product1_brand"),
        new Timestamp(Instant.now())
    );
    // and
    var productId = new ProductId(UUID.randomUUID());
    var productName = new ProductName("Product2");
    var productBrand = new ProductBrand("Product2_brand");
    var modifiedEvent = new ProductModified(
        new DomainEventHeader(
            new EventId(1L),
            new Timestamp(Instant.now())
        ),
        productId,
        productName,
        productBrand
    );

    // when
    product.dispatchAndHandle(modifiedEvent);

    // then
    assertThat(product.id()).isEqualTo(productId);
    assertThat(product.name()).isEqualTo(productName);
    assertThat(product.brand()).isEqualTo(productBrand);
  }

  @Test
  void should_restore_product() {
    // given
    var productId = new ProductId(UUID.randomUUID());
    var productBrand = new ProductBrand("Product1_brand");
    var modifiedEvent = new ProductModified(
        new DomainEventHeader(
            new EventId(1L),
            new Timestamp(Instant.now())
        ),
        productId,
        new ProductName("Product1"),
        productBrand
    );
    // and
    var modifiedNameEvent = new ProductModified(
        new DomainEventHeader(
            new EventId(2L),
            new Timestamp(Instant.now())
        ),
        productId,
        new ProductName("Product2"),
        productBrand
    );

    // when
    var product = Product.restore(List.of(modifiedEvent, modifiedNameEvent), new Version(1L));

    // then
    assertThat(product.id()).isEqualTo(productId);
    assertThat(product.name()).isEqualTo(modifiedNameEvent.name());
    assertThat(product.brand()).isEqualTo(productBrand);
  }
}