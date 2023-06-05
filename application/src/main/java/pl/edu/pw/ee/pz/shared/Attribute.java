package pl.edu.pw.ee.pz.shared;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.math.BigDecimal;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute;

public record Attribute<T, V extends Attribute.AttributeValue<T>>(
    String type,
    V value
) {

  @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = StringAttributeValue.class, name = "String"),
      @JsonSubTypes.Type(value = IntegerAttributeValue.class, name = "Integer"),
      @JsonSubTypes.Type(value = LongAttributeValue.class, name = "Long"),
      @JsonSubTypes.Type(value = BigDecimalAttributeValue.class, name = "BigDecimal"),
  })
  public sealed interface AttributeValue<T> {

    T value();

    @SuppressWarnings("unchecked")
    static <T, V extends AttributeValue<T>> V of(VariationAttribute.AttributeValue<?> value) {
      requireNonNull(value);
      if (value instanceof VariationAttribute.StringAttributeValue stringAttributeValue) {
        return (V) stringAttribute(stringAttributeValue.value());
      } else if (value instanceof VariationAttribute.IntegerAttributeValue integerAttributeValue) {
        return (V) integerAttribute(integerAttributeValue.value());
      } else if (value instanceof VariationAttribute.LongAttributeValue longAttributeValue) {
        return (V) longAttribute(longAttributeValue.value());
      } else if (value instanceof VariationAttribute.BigDecimalAttributeValue bigDecimalAttributeValue) {
        return (V) bigDecimalAttribute(bigDecimalAttributeValue.value());
      } else {
        throw new IllegalArgumentException("Cannot map value type %s: %s".formatted(
            value.getClass().getSimpleName(),
            value.value()
        ));
      }
    }

    @SuppressWarnings("unchecked")
    default <V extends VariationAttribute.AttributeValue<T>> V toDomainAttributeValue() {
      if (this instanceof StringAttributeValue stringAttributeValue) {
        return (V) VariationAttribute.AttributeValue.stringAttribute(stringAttributeValue.value());
      } else if (this instanceof IntegerAttributeValue integerAttributeValue) {
        return (V) VariationAttribute.AttributeValue.integerAttribute(integerAttributeValue.value());
      } else if (this instanceof LongAttributeValue longAttributeValue) {
        return (V) VariationAttribute.AttributeValue.longAttribute(longAttributeValue.value());
      } else if (this instanceof BigDecimalAttributeValue bigDecimalAttributeValue) {
        return (V) VariationAttribute.AttributeValue.bigDecimalAttribute(bigDecimalAttributeValue.value());
      } else {
        throw new IllegalArgumentException("Cannot map value type %s: %s".formatted(
            this.getClass().getSimpleName(),
            this.value()
        ));
      }
    }

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
