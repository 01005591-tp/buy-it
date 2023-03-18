package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.store.Product.ProductBrand;
import pl.edu.pw.ee.pz.store.Product.ProductId;
import pl.edu.pw.ee.pz.store.Product.ProductName;

public record ProductModified(
    DomainEventHeader eventHeader,
    ProductId id,
    ProductName name,
    ProductBrand brand
) implements DomainEvent {

}
