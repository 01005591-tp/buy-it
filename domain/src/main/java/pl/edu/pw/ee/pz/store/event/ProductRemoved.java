package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

public record ProductRemoved(
    DomainEventHeader<StoreId> header,
    ProductId product
) implements DomainEvent<StoreId> {

}
