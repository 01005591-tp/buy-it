package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/products")
@RequiredArgsConstructor
public class ProductResource {

  private final CreateProductEndpoint createProductEndpoint;
  private final UpdateProductEndpoint updateProductEndpoint;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<CreateProductResponse>> create(CreateProductRequest request) {
    return createProductEndpoint.handle(request);
  }

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<Void>> update(@RestPath String id, UpdateProductRequest request) {
    return updateProductEndpoint.handle(id, request);
  }
}
