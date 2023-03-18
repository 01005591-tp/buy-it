package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;

public record StoreAddressChanged(
    DomainEventHeader eventHeader,
    Address address
) implements DomainEvent {

}
