package pl.edu.pw.ee.pz.product;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandlerExecutor;
import pl.edu.pw.ee.pz.sharedkernel.function.UncheckedFunction;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

@RequiredArgsConstructor(access = PACKAGE)
class CreateProductEndpoint {

  private final CommandHandlerExecutor commandHandlerExecutor;
  private final ProductVariationsMapper productVariationsMapper;

  Uni<RestResponse<CreateProductResponse>> handle(CreateProductRequest request) {
    var command = toNewProductCommand(request);
    return commandHandlerExecutor.execute(command)
        .onItem().transform(UncheckedFunction.from(result -> {
          if (result instanceof ProductId productId) {
            return RestResponse.status(Status.CREATED, new CreateProductResponse(productId.value()));
          } else {
            throw new RuntimeException("Result is not a ProductId instance: %s".formatted(result.getClass().getName()));
          }
        }));
  }

  private NewProductCommand toNewProductCommand(CreateProductRequest request) {
    return new NewProductCommand(
        new ProductCode(request.code()),
        new BrandId(UUID.fromString(request.brandId())),
        productVariationsMapper.toProductVariations(request.variations())
    );
  }
}
