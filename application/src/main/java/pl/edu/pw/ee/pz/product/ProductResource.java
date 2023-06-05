package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import pl.edu.pw.ee.pz.HttpApiError;

@Path("/products")
@RequiredArgsConstructor
public class ProductResource {

  private final CreateProductEndpoint createProductEndpoint;
  private final UpdateProductEndpoint updateProductEndpoint;
  private final SearchProductByIdEndpoint searchProductByIdEndpoint;
  private final SearchProductByManagingCriteriaEndpoint searchProductByManagingCriteriaEndpoint;

  @APIResponse(
      description = "Product created",
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CreateProductResponse.class)
      )
  )
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<?>> create(CreateProductRequest request) {
    return createProductEndpoint.handle(request);
  }

  @APIResponse(description = "Product updated", responseCode = "204")
  @APIResponse(description = "Product not found", responseCode = "404")
  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<?>> update(@RestPath String id, UpdateProductRequest request) {
    return updateProductEndpoint.handle(id, request);
  }

  @APIResponse(
      description = "Product found",
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = SearchProductByIdResponse.class)
      )
  )
  @APIResponse(description = "Product not found", responseCode = "404")
  @APIResponse(
      description = "Server error. Expected codes",
      responseCode = "500",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = HttpApiError.class)
      )
  )
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<?>> searchById(@RestPath String id) {
    return searchProductByIdEndpoint.handle(id);
  }

  @APIResponse(
      description = "Products found",
      responseCode = "200",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = SearchProductsForManagingResponse.class))
  )
  @APIResponse(description = "No product found", responseCode = "204")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<?>> searchByManagingCriteria(
      @BeanParam SearchProductForManagingRequest request
  ) {
    return searchProductByManagingCriteriaEndpoint.handle(request);
  }
}
