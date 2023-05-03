package pl.edu.pw.ee.pz.product.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

public record ProductVariationRemoved(
    DomainEventHeader<ProductId> header,
    ProductVariation variation
) implements DomainEvent<ProductId> {

}
