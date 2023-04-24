package pl.edu.pw.ee.pz.product.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

public record ProductCodeChanged(
    DomainEventHeader<ProductId> header,
    ProductCode code
) implements DomainEvent<ProductId> {

}
