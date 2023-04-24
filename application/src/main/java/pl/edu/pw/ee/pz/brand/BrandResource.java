package pl.edu.pw.ee.pz.brand;

import io.smallrye.mutiny.Uni;
import java.io.InputStream;
import java.nio.file.Files;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandlerExecutor;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
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
  public Uni<Void> create(
      @RestForm("logoImage") @PartType(MediaType.MULTIPART_FORM_DATA) FileUpload logoImage,
      @RestForm @PartType(MediaType.TEXT_PLAIN) String code
  ) {
    return readLogoInputStream(logoImage)
        .onItem().transform(logoImageInputStream -> toNewBrandCommand(code, logoImageInputStream, logoImage))
        .onItem().transformToUni(this::execute);
  }

  private Uni<Void> execute(NewBrandCommand command) {
    return commandHandlerExecutor.execute(command);
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
