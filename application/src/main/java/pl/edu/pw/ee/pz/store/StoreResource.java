package pl.edu.pw.ee.pz.store;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/stores")
@RequiredArgsConstructor
public class StoreResource {

  @POST
  @Path("/{id}/products")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<Void>> updateProductsAvailability(
      @RestPath String id,
      UpdateProductAvailabilityRequest request
  ) {
    return Uni.createFrom().failure(new UnsupportedOperationException("Not yet implemented"));
  }

  @GET
  @Path("/{id}/products")
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<GetProductsWithAvailabilityResponse>> getProductsWithAvailability(@RestPath String id) {
    return Uni.createFrom().failure(new UnsupportedOperationException("Not yet implemented"));
  }
}
