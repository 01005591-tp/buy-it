package pl.edu.pw.ee.pz.product.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

public record ProductBrandChanged(
    DomainEventHeader<ProductId> header,
    BrandId brand
) implements DomainEvent<ProductId> {

}
