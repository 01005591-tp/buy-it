package pl.edu.pw.ee.pz.product.event;

import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;

public record ProductVariationsReplaced(
    DomainEventHeader<ProductId> header,
    List<ProductVariation> variations
) implements DomainEvent<ProductId> {

}
