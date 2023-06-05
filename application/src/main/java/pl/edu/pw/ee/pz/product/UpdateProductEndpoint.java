package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;
import pl.edu.pw.ee.pz.product.port.ProductNotFoundException;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandlerExecutor;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

@RequiredArgsConstructor(access = PACKAGE)
class UpdateProductEndpoint {

  private final CommandHandlerExecutor commandHandlerExecutor;
  private final ProductVariationsMapper productVariationsMapper;

  Uni<RestResponse<?>> handle(String id, UpdateProductRequest request) {
    var productId = new ProductId(UUID.fromString(id));
    return commandHandlerExecutor.execute(new UpdateProductCommand(
            productId,
            new ProductCode(request.code()),
            new BrandId(UUID.fromString(request.brandId())),
            productVariationsMapper.toProductVariations(request.variations())
        ))
        .onItem().<RestResponse<?>>transform(success -> RestResponse.noContent())
        .onFailure(ProductNotFoundException.class).recoverWithItem(notFound -> RestResponse.notFound());
  }
}
