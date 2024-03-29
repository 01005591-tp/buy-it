package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreCode;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

public record StoreCreated(
    DomainEventHeader<StoreId> header,
    StoreCode code,
    Address address
) implements DomainEvent<StoreId> {

}
