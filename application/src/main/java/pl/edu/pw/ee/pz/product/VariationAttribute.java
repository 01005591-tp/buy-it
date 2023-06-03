package pl.edu.pw.ee.pz.product;

import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

record VariationAttribute(
    @Parameter(required = true)
    String type,
    String value,
    @Parameter(description = "Value type. STRING if not specified")
    ValueType valueType
) {

  enum ValueType {
    STRING,
    LONG,
    INTEGER,
    BIG_DECIMAL
  }
}
