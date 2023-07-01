package pl.edu.pw.ee.pz.store;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/stores")
@RequiredArgsConstructor
public class StoreResource {

  private final UpdateProductsAvailablePiecesEndpoint updateProductsAvailablePiecesEndpoint;
  private final CreateStoreEndpoint createStoreEndpoint;

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

  @APIResponse(
      description = "Product created",
      responseCode = "201",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON,
          schema = @Schema(implementation = CreateStoreResponse.class)
      )
  )
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<?>> create(CreateStoreRequest request) {
    return createStoreEndpoint.handle(request);
  }
}
