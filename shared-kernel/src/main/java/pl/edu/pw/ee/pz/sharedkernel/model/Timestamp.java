package pl.edu.pw.ee.pz.sharedkernel.model;

import java.time.Instant;

public record Timestamp(Instant value) {

  public static Timestamp now() {
    return new Timestamp(Instant.now());
  }
}
