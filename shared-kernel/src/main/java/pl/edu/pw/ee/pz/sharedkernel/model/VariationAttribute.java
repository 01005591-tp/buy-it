package pl.edu.pw.ee.pz.sharedkernel.model;

public record VariationAttribute(
    AttributeType type,
    AttributeValue value
) {

  public record AttributeType(String value) {

  }

  public record AttributeValue(String value) {

  }
}
