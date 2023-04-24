package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation.VariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;
import pl.edu.pw.ee.pz.store.ProductVariationPieces;

public record ProductVariationPiecesRemoved(
    DomainEventHeader<StoreId> header,
    ProductId product,
    VariationId variation,
    ProductVariationPieces pieces
) implements DomainEvent<StoreId> {

}
