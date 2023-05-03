package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;
import pl.edu.pw.ee.pz.store.ProductVariationPieces;

public record ProductVariationPiecesAdded(
    DomainEventHeader<StoreId> header,
    ProductId product,
    ProductVariation variation,
    ProductVariationPieces pieces
) implements DomainEvent<StoreId> {

}
