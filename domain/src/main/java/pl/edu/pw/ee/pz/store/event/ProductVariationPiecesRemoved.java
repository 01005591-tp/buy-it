package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.sharedkernel.model.Pieces;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariationId;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

public record ProductVariationPiecesRemoved(
    DomainEventHeader<StoreId> header,
    ProductId product,
    ProductVariationId variation,
    Pieces pieces
) implements DomainEvent<StoreId> {

}
