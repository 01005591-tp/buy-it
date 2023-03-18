package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.store.Product.ProductId;

public record ProductRemoved(
    DomainEventHeader eventHeader,
    ProductId productId
) implements DomainEvent {

}
