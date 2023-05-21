package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.ee.pz.product.event.ProductBrandChanged;
import pl.edu.pw.ee.pz.product.event.ProductCodeChanged;
import pl.edu.pw.ee.pz.product.event.ProductCreated;
import pl.edu.pw.ee.pz.product.event.ProductVariationAdded;
import pl.edu.pw.ee.pz.product.event.ProductVariationRemoved;
import pl.edu.pw.ee.pz.product.event.ProductVariationsReplaced;
import pl.edu.pw.ee.pz.product.port.ProductAggregatePort;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.Projection;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class ProductProjection implements Projection {

  private final ProductProjectionPort productProjectionPort;
  private final ProductAggregatePort productAggregatePort;

  @Override
  public List<Class<? extends DomainEvent<?>>> supportedEvents() {
    return List.of(
        ProductCreated.class,
        ProductVariationAdded.class,
        ProductVariationRemoved.class,
        ProductVariationsReplaced.class,
        ProductCodeChanged.class,
        ProductBrandChanged.class
    );
  }

  @Override
  public Uni<Void> handle(DomainEvent<?> event) {
    if (event instanceof ProductCreated productCreated) {
      return handle(productCreated)
          .onFailure().call(throwable -> recover(productCreated, throwable));
    } else if (event instanceof ProductVariationAdded variationAdded) {
      return handle(variationAdded)
          .onFailure().call(throwable -> recover(variationAdded, throwable));
    } else if (event instanceof ProductVariationRemoved variationRemoved) {
      return handle(variationRemoved)
          .onFailure().call(throwable -> recover(variationRemoved, throwable));
    } else if (event instanceof ProductVariationsReplaced variationsReplaced) {
      return handle(variationsReplaced)
          .onFailure().call(throwable -> recover(variationsReplaced, throwable));
    } else if (event instanceof ProductCodeChanged codeChanged) {
      return handle(codeChanged)
          .onFailure().call(throwable -> recover(codeChanged, throwable));
    } else if (event instanceof ProductBrandChanged brandChanged) {
      return handle(brandChanged)
          .onFailure().call(throwable -> recover(brandChanged, throwable));
    }
    log.debug("Ignored unsupported event {}", event.getClass().getSimpleName());
    return Uni.createFrom().voidItem();
  }

  private Uni<Void> handle(ProductCreated event) {
    var product = new Product(
        event.header().aggregateId(),
        event.code(),
        event.brand()
    );
    return productProjectionPort.addProduct(product)
        .onItem().invoke(() -> log.info("Product stored {}", product.id().value()));
  }

  private Uni<Void> handle(ProductVariationAdded event) {
    return productProjectionPort.addVariation(event.header().aggregateId(), event.productVariation())
        .onItem().invoke(() -> log.info(
            "Product variation added. Product: {}, Variation: {}",
            event.header().aggregateId().value(),
            event.productVariation().id().value()
        ));
  }

  private Uni<Void> handle(ProductVariationRemoved event) {
    return productProjectionPort.removeVariation(event.header().aggregateId(), event.variation().id())
        .onItem().invoke(() -> log.info(
            "Product variation removed. Product: {}, Variation: {}",
            event.header().aggregateId().value(),
            event.variation().id().value()
        ));
  }

  private Uni<Void> handle(ProductVariationsReplaced event) {
    return productProjectionPort.replaceVariations(event.header().aggregateId(), event.variations())
        .onItem().invoke(() -> log.info(
            "Product variations replaced. Product: {}", event.header().aggregateId().value()
        ));
  }

  private Uni<Void> handle(ProductCodeChanged event) {
    return productProjectionPort.changeCode(event.header().aggregateId(), event.code())
        .onItem().invoke(() -> log.info(
            "Product code changed. Product: {}, Code: {}", event.header().aggregateId().value(), event.code().value()
        ));
  }

  private Uni<Void> handle(ProductBrandChanged event) {
    return productProjectionPort.changeBrand(event.header().aggregateId(), event.brand())
        .onItem().invoke(() -> log.info(
            "Product brand changed. Product: {}, Brand: {}", event.header().aggregateId().value(), event.brand().value()
        ));
  }

  private Uni<Void> recover(DomainEvent<ProductId> event, Throwable throwable) {
    log.warn("Could not process event {}", event, throwable);
    var productId = event.header().aggregateId();
    return productAggregatePort.findById(productId)
        .onItem().transform(productAggregate -> new Product(
            productAggregate.id(),
            productAggregate.code(),
            productAggregate.brand(),
            productAggregate.variations()
        ))
        .onItem().transformToUni(productProjectionPort::addProduct)
        .onItem().invoke(() -> log.info("Recovered from error during event processing {}", event));
  }
}
