package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.store.Product.ProductId;
import pl.edu.pw.ee.pz.store.ProductVariation.VariationId;

public record ProductVariationRemoved(
    DomainEventHeader eventHeader,
    ProductId product,
    VariationId variation
) implements DomainEvent {

}
