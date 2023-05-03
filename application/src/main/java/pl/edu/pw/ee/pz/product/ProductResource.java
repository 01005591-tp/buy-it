package pl.edu.pw.ee.pz.product;

import io.smallrye.mutiny.Uni;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
