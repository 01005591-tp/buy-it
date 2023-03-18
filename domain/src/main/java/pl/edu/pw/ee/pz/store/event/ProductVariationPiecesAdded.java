package pl.edu.pw.ee.pz.store.event;

import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;
import pl.edu.pw.ee.pz.store.Product.ProductId;
import pl.edu.pw.ee.pz.store.ProductVariation.VariationId;
import pl.edu.pw.ee.pz.store.ProductVariationPieces;

public record ProductVariationPiecesAdded(
    DomainEventHeader eventHeader,
    ProductId product,
    VariationId variation,
    ProductVariationPieces pieces
) implements DomainEvent {

}
