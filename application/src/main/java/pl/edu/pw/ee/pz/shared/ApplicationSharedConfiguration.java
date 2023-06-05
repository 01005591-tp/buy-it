package pl.edu.pw.ee.pz.shared;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ApplicationSharedConfiguration {

  @Produces
  public ProductDtoMapper productDtoMapper() {
    return new ProductDtoMapper();
  }
}
