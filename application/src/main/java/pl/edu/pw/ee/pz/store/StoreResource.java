package pl.edu.pw.ee.pz.store;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/stores")
@RequiredArgsConstructor
public class StoreResource {

  private final UpdateProductsAvailablePiecesEndpoint updateProductsAvailablePiecesEndpoint;

  @Operation(description = "Update product variation available pieces")
  @APIResponse(
      description = "Product variation availabilities updated",
      responseCode = "200"
  )
  @PUT
  @Path("/{id}/products/pieces")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<?>> updateProductsAvailablePieces(
      @RestPath String id,
      UpdateProductsAvailabilityRequest request
  ) {
    return updateProductsAvailablePiecesEndpoint.handle(id, request);
  }

  @GET
  @Path("/{id}/products")
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<GetProductsWithAvailabilityResponse>> getProducts(
      @RestPath String id,
      @BeanParam SearchStoreProductAvailabilitiesRequest request
  ) {
    return Uni.createFrom().failure(new UnsupportedOperationException("Not yet implemented"));
  }
}
