package pl.edu.pw.ee.pz.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.product.event.ProductCreated;
import pl.edu.pw.ee.pz.product.event.ProductVariationAdded;
import pl.edu.pw.ee.pz.product.event.ProductVariationRemoved;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.DomainEventHeader;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation.VariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.Timestamp;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeType;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeValue;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;
import pl.edu.pw.ee.pz.store.error.ProductVariationAlreadyExistsException;
import pl.edu.pw.ee.pz.store.error.ProductVariationMissingException;

class ProductAggregateTest {

  @Test
  void should_create_new_product() {
    // given
    var productId = new ProductId(UUID.randomUUID());
    var productBrand = new BrandCode("MIGHTY_BRAND");
    var productCode = new ProductCode("MIGHTY_SHOES_LIMITED_EDITION");

    // when
    var product = new ProductAggregate(
        productId,
        productCode,
        productBrand
    );

    // then
    assertThat(product.id()).isEqualTo(productId);
    assertThat(product.code()).isEqualTo(productCode);
    assertThat(product.brand()).isEqualTo(productBrand);
  }

  @Test
  void should_add_product_variation() {
    // given
    var product = newProduct();
    // and
    var productVariation = new ProductVariation(
        new VariationId(UUID.randomUUID()),
        Set.of(
            new VariationAttribute(
                new AttributeType("SIZE"),
                new AttributeValue("42")
            ),
            new VariationAttribute(
                new AttributeType("COLOR1"),
                new AttributeValue("BROWN")
            ),
            new VariationAttribute(
                new AttributeType("COLOR2"),
                new AttributeValue("GREEN")
            )
        )
    );

    // when
    product.addVariation(productVariation);

    // then
    assertThat(product.variations())
        .hasSize(1)
        .allSatisfy((variationId, variation) -> {
          assertThat(variationId).isEqualTo(productVariation.id());
          assertThat(variation.id()).isEqualTo(productVariation.id());
          assertThat(variation.attributes()).containsExactlyInAnyOrderElementsOf(productVariation.attributes());
        });
  }

  @Test
  void should_fail_adding_product_variation_when_such_already_exists() {
    // given
    var product = newProduct();
    // and
    var productVariation = new ProductVariation(
        new VariationId(UUID.randomUUID()),
        Set.of(
            new VariationAttribute(
                new AttributeType("SIZE"),
                new AttributeValue("42")
            ),
            new VariationAttribute(
                new AttributeType("COLOR1"),
                new AttributeValue("BROWN")
            ),
            new VariationAttribute(
                new AttributeType("COLOR2"),
                new AttributeValue("GREEN")
            )
        )
    );
    // and
    product.addVariation(productVariation);

    // when
    var throwableAssert = assertThatCode(() -> product.addVariation(productVariation));

    // then
    throwableAssert
        .isInstanceOf(ProductVariationAlreadyExistsException.class)
        .hasMessage("Product variation %s already exists for product %s.".formatted(
            productVariation.id().value().toString(),
            product.id().value()
        ));
  }

  @Test
  void should_fail_removing_variation_when_not_exists() {
    // given
    var product = newProduct();
    // and
    var variationId = new VariationId(UUID.randomUUID());

    // when
    var throwableAssert = assertThatCode(() -> product.removeVariation(variationId));

    // then
    throwableAssert
        .isInstanceOf(ProductVariationMissingException.class)
        .hasMessage("Variation %s not defined for product %s".formatted(
            variationId.value().toString(),
            product.id().value()
        ));
  }

  @Test
  void should_restore_product() {
    // given
    var productId = new ProductId(UUID.randomUUID());
    var productCreated = new ProductCreated(
        newDomainEventHeader(productId),
        new ProductCode("MIGHTY_SHOES_LIMITED_EDITION"),
        new BrandCode("MIGHTY_BRAND")
    );
    // and
    var productVariation1Added = new ProductVariationAdded(
        newDomainEventHeader(productId),
        new ProductVariation(
            new VariationId(UUID.randomUUID()),
            Set.of(
                new VariationAttribute(
                    new AttributeType("SIZE"),
                    new AttributeValue("42")
                ),
                new VariationAttribute(
                    new AttributeType("COLOR1"),
                    new AttributeValue("BROWN")
                ),
                new VariationAttribute(
                    new AttributeType("COLOR2"),
                    new AttributeValue("GREEN")
                )
            )
        )
    );
    // and
    var productVariation2Added = new ProductVariationAdded(
        newDomainEventHeader(productId),
        new ProductVariation(
            new VariationId(UUID.randomUUID()),
            Set.of(
                new VariationAttribute(
                    new AttributeType("SIZE"),
                    new AttributeValue("40")
                ),
                new VariationAttribute(
                    new AttributeType("COLOR1"),
                    new AttributeValue("BLACK")
                ),
                new VariationAttribute(
                    new AttributeType("COLOR2"),
                    new AttributeValue("WHITE")
                )
            )
        )
    );
    // and
    var productVariation1Removed = new ProductVariationRemoved(
        newDomainEventHeader(productId),
        productVariation1Added.productVariation().id()
    );
    // and
    var events = List.<DomainEvent<ProductId>>of(
        productCreated,
        productVariation1Added,
        productVariation2Added,
        productVariation1Removed
    );

    // when
    var product = ProductAggregate.restore(events, Version.initial(), ProductAggregate::new);

    // then
    assertThat(product.id()).isEqualTo(productCreated.header().aggregateId());
    assertThat(product.code()).isEqualTo(productCreated.code());
    assertThat(product.brand()).isEqualTo(productCreated.brand());
    assertThat(product.variations())
        .hasSize(1)
        .allSatisfy((variationId, variation) -> {
          assertThat(variationId).isEqualTo(productVariation2Added.productVariation().id());
          assertThat(variation.id()).isEqualTo(productVariation2Added.productVariation().id());
          assertThat(variation.attributes())
              .containsExactlyInAnyOrderElementsOf(productVariation2Added.productVariation().attributes());
        });
  }

  private <ID extends AggregateId> DomainEventHeader<ID> newDomainEventHeader(ID aggregateId) {
    return new DomainEventHeader<>(
        new EventId(1L),
        aggregateId,
        new Timestamp(Instant.now())
    );
  }

  private ProductAggregate newProduct() {
    return new ProductAggregate(
        new ProductId(UUID.randomUUID()),
        new ProductCode("MIGHTY_SHOES_LIMITED_EDITION"),
        new BrandCode("MIGHTY_BRAND")
    );
  }
}