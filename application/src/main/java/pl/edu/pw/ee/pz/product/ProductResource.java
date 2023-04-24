package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandlerExecutor;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductCode;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeType;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeValue;

@GraphQLApi
@RequiredArgsConstructor
public class ProductResource {

  private final CommandHandlerExecutor commandHandlerExecutor;

  @Query
  public Uni<CreateProductRequest> createRequest(CreateProductRequest request) {
    return Uni.createFrom().item(request);
  }

  @Mutation
  @Description("Create Product")
  public Uni<String> create(CreateProductRequest request) {
    var command = toNewProductCommand(request);
    return commandHandlerExecutor.execute(command)
        .onItem().transform(success -> "");
  }

  @Mutation
  @Description("Update Product")
  public Uni<String> update(UpdateProductRequest request) {
    var command = new UpdateProductCommand(
        new ProductId(UUID.fromString(request.getId())),
        new ProductCode(request.getCode()),
        new BrandCode(request.getBrandCode()),
        request.getVariations().stream()
            .map(this::toNewProductVariation)
            .toList()
    );
    return commandHandlerExecutor.execute(command)
        .onItem().transform(success -> "");
  }

  private NewProductCommand toNewProductCommand(CreateProductRequest request) {
    return new NewProductCommand(
        new ProductCode(request.getCode()),
        new BrandCode(request.getBrandCode()),
        request.getVariations().stream()
            .map(this::toNewProductVariation)
            .toList()
    );
  }

  private NewProductVariation toNewProductVariation(Variation variation) {
    return variation.getAttributes().stream()
        .map(this::toVariationAttribute)
        .collect(Collectors.collectingAndThen(Collectors.toUnmodifiableSet(), NewProductVariation::new));
  }

  private VariationAttribute toVariationAttribute(pl.edu.pw.ee.pz.product.VariationAttribute attribute) {
    return new VariationAttribute(
        new AttributeType(attribute.getType()),
        new AttributeValue(attribute.getValue())
    );
  }
}
