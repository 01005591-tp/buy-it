package pl.edu.pw.ee.pz.brand;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.brand.port.BrandAggregatePort;
import pl.edu.pw.ee.pz.file.FileContent;
import pl.edu.pw.ee.pz.file.FileName;
import pl.edu.pw.ee.pz.file.FilePath;
import pl.edu.pw.ee.pz.file.FileService;
import pl.edu.pw.ee.pz.file.FileSpace;
import pl.edu.pw.ee.pz.file.UploadFile;
import pl.edu.pw.ee.pz.file.UploadFile.Size;
import pl.edu.pw.ee.pz.file.UploadFileCommand;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;

@RequiredArgsConstructor
public class NewBrandCommandHandler implements CommandHandler<NewBrandCommand, BrandId> {

  // TODO: move it to external properties
  private static final String BRANDS_FILES_SPACE = "brands";
  private static final String LOGO_FILE_EXTENSION = "logo";
  private final FileService fileService;
  private final BrandAggregatePort brandAggregatePort;

  @Override
  public Uni<BrandId> handle(NewBrandCommand command) {
    var brand = new BrandAggregate(new BrandId(UUID.randomUUID()), command.code());
    return uploadLogo(command, brand)
        .onItem().transformToUni(success -> brandAggregatePort.save(brand))
        .onItem().transform(success -> brand.id());
  }

  private Uni<Void> uploadLogo(NewBrandCommand command, BrandAggregate brand) {
    var name = new FileName("%s.%s".formatted(brand.id().value(), LOGO_FILE_EXTENSION));
    var path = new FilePath(
        new FileSpace(BRANDS_FILES_SPACE)
    );
    var content = new FileContent(command.logo().content().value());
    var size = new Size(command.logo().size().value());
    var file = new UploadFile(name, path, content, size);
    return fileService.upload(new UploadFileCommand(file));
  }
}
