package pl.edu.pw.ee.pz.brand;

import io.smallrye.mutiny.Uni;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import pl.edu.pw.ee.pz.brand.port.BrandNotFoundException;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandlerExecutor;
import pl.edu.pw.ee.pz.sharedkernel.function.UncheckedFunction;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandLogo;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandLogo.BrandLogoContent;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandLogo.BrandLogoSize;
import pl.edu.pw.ee.pz.sharedkernel.mutiny.MutinyUtil;

@Path("/brands")
@RequiredArgsConstructor
public class BrandResource {

  private final CommandHandlerExecutor commandHandlerExecutor;


  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<CreateBrandResponse>> create(
      @RestForm("logoImage") @PartType(MediaType.MULTIPART_FORM_DATA) FileUpload logoImage,
      @RestForm @PartType(MediaType.TEXT_PLAIN) String code
  ) {
    return readLogoInputStream(logoImage)
        .onItem().transform(logoImageInputStream -> toNewBrandCommand(code, logoImageInputStream, logoImage))
        .onItem().transformToUni(commandHandlerExecutor::<BrandId>execute)
        .onItem().transform(UncheckedFunction.from(
            brandId -> RestResponse.status(Status.CREATED, new CreateBrandResponse(brandId.id().toString()))
        ));
  }

  @PUT
  @Path("/{id}/code")
  @Consumes(MediaType.APPLICATION_JSON)
  public Uni<RestResponse<Void>> changeBrandCode(ChangeBrandCodeRequest request, String id) {
    var command = new ChangeBrandCodeCommand(
        new BrandId(UUID.fromString(id)), new BrandCode(request.code()));
    return commandHandlerExecutor.execute(command)
        .onItem().<RestResponse<Void>>transform(success -> RestResponse.noContent())
        .onFailure(BrandNotFoundException.class).recoverWithItem(notFound -> RestResponse.notFound());
  }

  private Uni<InputStream> readLogoInputStream(FileUpload logoImage) {
    return MutinyUtil.uniFromCallable(() -> Files.newInputStream(logoImage.uploadedFile()));
  }

  private NewBrandCommand toNewBrandCommand(
      String requestCode, InputStream logoImageInputStream, FileUpload logoImage
  ) {
    var logo = new BrandLogo(new BrandLogoContent(logoImageInputStream), new BrandLogoSize(logoImage.size()));
    var code = new BrandCode(requestCode);
    return new NewBrandCommand(code, logo);
  }
}
