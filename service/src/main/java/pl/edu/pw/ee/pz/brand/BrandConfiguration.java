package pl.edu.pw.ee.pz.brand;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.brand.port.BrandAggregatePort;
import pl.edu.pw.ee.pz.file.FileService;

@ApplicationScoped
public class BrandConfiguration {

  @Produces
  NewBrandCommandHandler newBrandCommandHandler(FileService fileService, BrandAggregatePort brandAggregatePort) {
    return new NewBrandCommandHandler(fileService, brandAggregatePort);
  }

  @Produces
  ChangeBrandCodeCommandHandler changeBrandCodeCommandHandler(BrandAggregatePort brandAggregatePort) {
    return new ChangeBrandCodeCommandHandler(brandAggregatePort);
  }

  @Produces
  BrandProjection brandProjection(BrandProjectionPort brandProjectionPort, BrandAggregatePort brandAggregatePort) {
    return new BrandProjection(brandProjectionPort, brandAggregatePort);
  }
}
