package pl.edu.pw.ee.pz.sharedkernel.model;

import java.io.InputStream;

public record BrandLogo(
    BrandLogoContent content,
    BrandLogoSize size
) {

  public record BrandLogoContent(InputStream value) {

  }

  public record BrandLogoSize(long value) {

  }

}
