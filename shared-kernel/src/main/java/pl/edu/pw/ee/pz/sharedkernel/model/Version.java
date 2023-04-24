package pl.edu.pw.ee.pz.sharedkernel.model;

public record Version(Long value) {

  private static final Version INITIAL = new Version(0L);

  public static Version initial() {
    return INITIAL;
  }
}
