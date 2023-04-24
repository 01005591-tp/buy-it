package pl.edu.pw.ee.pz.brand.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;

public record BrandCreated(
    DomainEventHeader<BrandId> header,
    BrandCode code
) implements DomainEvent<BrandId> {

}
