package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.store.ProductVariation;

public record ProductVariationAdded(
    DomainEventHeader eventHeader,
    ProductVariation productVariation
) implements DomainEvent {

}
