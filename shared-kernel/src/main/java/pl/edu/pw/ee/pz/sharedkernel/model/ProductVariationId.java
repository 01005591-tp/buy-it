package pl.edu.pw.ee.pz.sharedkernel.model;

import java.util.UUID;

public record ProductVariationId(UUID id) {

  public String value() {
    return id.toString();
  }
}
