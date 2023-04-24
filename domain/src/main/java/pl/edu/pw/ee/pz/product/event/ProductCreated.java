package pl.edu.pw.ee.pz.product.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

public record ProductCreated(
    DomainEventHeader<ProductId> header,
    ProductCode code,
    BrandCode brand
) implements DomainEvent<ProductId> {

}
