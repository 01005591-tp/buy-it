package pl.edu.pw.ee.pz.store;

public record VariationAttribute(
    AttributeType type,
    AttributeValue value
) {

  public record AttributeType(String value) {

  }

  public record AttributeValue(String value) {

  }
}
