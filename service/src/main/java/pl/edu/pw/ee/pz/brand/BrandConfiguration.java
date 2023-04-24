package pl.edu.pw.ee.pz.brand;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.brand.port.BrandAggregatePort;
import pl.edu.pw.ee.pz.file.FileService;

@ApplicationScoped
public class BrandConfiguration {

  @Produces
  NewBrandCommandHandler newBrandCommandHandler(FileService fileService, BrandAggregatePort brandAggregatePort) {
    return new NewBrandCommandHandler(fileService, brandAggregatePort);
  }

  @Produces
  BrandProjection brandProjection(BrandProjectionPort brandProjectionPort, BrandAggregatePort brandAggregatePort) {
    return new BrandProjection(brandProjectionPort, brandAggregatePort);
  }
}
