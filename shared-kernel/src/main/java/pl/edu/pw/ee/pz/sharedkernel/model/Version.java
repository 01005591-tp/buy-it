package pl.edu.pw.ee.pz.sharedkernel.model;

public sealed interface Version {

  Long value();

  default boolean isInitial() {
    return InitialVersion.INSTANCE.equals(this);
  }

  static Version initial() {
    return InitialVersion.INSTANCE;
  }

  static Version specified(Long value) {
    return new SpecifiedVersion(value);
  }

  final class InitialVersion implements Version {

    private static final InitialVersion INSTANCE = new InitialVersion();

    private InitialVersion() {
    }

    @Override
    public Long value() {
      return 0L;
    }
  }

  record SpecifiedVersion(Long value) implements Version {

  }
}
