package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;
import pl.edu.pw.ee.pz.store.Store.StoreId;

public record StoreCreated(
    DomainEventHeader eventHeader,
    StoreId id,
    Address address
) implements DomainEvent {

}
