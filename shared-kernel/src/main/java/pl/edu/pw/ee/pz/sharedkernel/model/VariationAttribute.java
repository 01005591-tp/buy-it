package pl.edu.pw.ee.pz.sharedkernel.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.math.BigDecimal;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.AttributeValue;

public record VariationAttribute<T, V extends AttributeValue<T>>(
    AttributeType type,
    V value
) {

  public record AttributeType(String value) {

  }

  @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = StringAttributeValue.class, name = "String"),
      @JsonSubTypes.Type(value = IntegerAttributeValue.class, name = "Integer"),
      @JsonSubTypes.Type(value = LongAttributeValue.class, name = "Long"),
      @JsonSubTypes.Type(value = BigDecimalAttributeValue.class, name = "BigDecimal"),
  })
  public sealed interface AttributeValue<T> {

    T value();

    static StringAttributeValue stringAttribute(String value) {
      return new StringAttributeValue(value);
    }

    static IntegerAttributeValue integerAttribute(Integer value) {
      return new IntegerAttributeValue(value);
    }

    static LongAttributeValue longAttribute(Long value) {
      return new LongAttributeValue(value);
    }

    static BigDecimalAttributeValue bigDecimalAttribute(BigDecimal value) {
      return new BigDecimalAttributeValue(value);
    }
  }

  public record StringAttributeValue(String value) implements AttributeValue<String> {

  }

  protected sealed interface NumberAttributeValue<T extends Number> extends AttributeValue<T> {

  }

  public record IntegerAttributeValue(Integer value) implements NumberAttributeValue<Integer> {

  }

  public record LongAttributeValue(Long value) implements NumberAttributeValue<Long> {

  }

  public record BigDecimalAttributeValue(BigDecimal value) implements NumberAttributeValue<BigDecimal> {

  }
}
