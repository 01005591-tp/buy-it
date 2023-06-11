package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationPieces;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

public record ProductVariationPiecesUpdated(
    DomainEventHeader<StoreId> header,
    ProductVariationPieces productVariationPieces
) implements DomainEvent<StoreId> {

}
