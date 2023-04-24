package pl.edu.pw.ee.pz.product;

import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PACKAGE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.product.event.ProductBrandChanged;
import pl.edu.pw.ee.pz.product.event.ProductCodeChanged;
import pl.edu.pw.ee.pz.product.event.ProductCreated;
import pl.edu.pw.ee.pz.product.event.ProductVariationAdded;
import pl.edu.pw.ee.pz.product.event.ProductVariationRemoved;
import pl.edu.pw.ee.pz.product.event.ProductVariationsReplaced;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateRoot;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateType;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent.EventId;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation.VariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.Version;
import pl.edu.pw.ee.pz.store.error.ProductVariationAlreadyExistsException;
import pl.edu.pw.ee.pz.store.error.ProductVariationMissingException;

@Accessors(fluent = true)
public class ProductAggregate extends AggregateRoot<ProductId> {

  private static final AggregateType AGGREGATE_TYPE = new AggregateType("product");

  @Getter(PACKAGE)
  private ProductCode code;
  @Getter(PACKAGE)
  private BrandCode brand;
  @Getter(PACKAGE)
  private final Map<VariationId, ProductVariation> variations = new HashMap<>();

  ProductAggregate(Version version, EventId latestEvent) {
    super(AGGREGATE_TYPE, version, latestEvent);
  }

  public ProductAggregate(ProductId id, ProductCode code, BrandCode brand) {
    this(Version.initial(), EventId.initial());
    var eventHeader = nextDomainEventHeader(id);
    var created = new ProductCreated(eventHeader, code, brand);
    handleAndRegisterEvent(created);
  }

  public void addVariation(ProductVariation variation) {
    var variationId = variation.id();
    if (variations.containsKey(variationId)) {
      throw ProductVariationAlreadyExistsException.alreadyExists(this.id, variationId);
    }
    handleAndRegisterEvent(new ProductVariationAdded(
        nextDomainEventHeader(),
        variation
    ));
  }

  public void removeVariation(VariationId variationId) {
    if (!this.variations.containsKey(variationId)) {
      throw ProductVariationMissingException.variationMissing(this.id, variationId);
    }
    handleAndRegisterEvent(new ProductVariationRemoved(
        nextDomainEventHeader(),
        variationId
    ));
  }

  public void updateVariations(List<ProductVariation> variations) {
    handleAndRegisterEvent(new ProductVariationsReplaced(
        nextDomainEventHeader(),
        variations
    ));
  }

  public void changeCode(ProductCode code) {
    handleAndRegisterEvent(new ProductCodeChanged(
        nextDomainEventHeader(),
        code
    ));
  }

  public void changeBrand(BrandCode brand) {
    handleAndRegisterEvent(new ProductBrandChanged(
        nextDomainEventHeader(),
        brand
    ));
  }

  @Override
  protected void handle(DomainEvent event) {
    if (event instanceof ProductCreated created) {
      handle(created);
    } else if (event instanceof ProductVariationAdded variationAdded) {
      handle(variationAdded);
    } else if (event instanceof ProductVariationRemoved variationRemoved) {
      handle(variationRemoved);
    } else if (event instanceof ProductVariationsReplaced variationsReplaced) {
      handle(variationsReplaced);
    } else if (event instanceof ProductCodeChanged codeChanged) {
      handle(codeChanged);
    } else if (event instanceof ProductBrandChanged brandChanged) {
      handle(brandChanged);
    }
  }

  private void handle(ProductCreated event) {
    this.id = event.header().aggregateId();
    this.code = event.code();
    this.brand = event.brand();
  }

  private void handle(ProductVariationAdded event) {
    variations.put(event.productVariation().id(), event.productVariation());
  }

  private void handle(ProductVariationRemoved event) {
    variations.remove(event.variation());
  }

  private void handle(ProductVariationsReplaced event) {
    variations.clear();
    var newVariations = event.variations().stream()
        .collect(toMap(ProductVariation::id, Function.identity()));
    variations.putAll(newVariations);
  }

  private void handle(ProductCodeChanged event) {
    this.code = event.code();
  }

  private void handle(ProductBrandChanged event) {
    this.brand = event.brand();
  }

  public static AggregateType aggregateType() {
    return AGGREGATE_TYPE;
  }
}
