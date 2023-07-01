package pl.edu.pw.ee.pz.shared;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.math.BigDecimal;
import pl.edu.pw.ee.pz.shared.AttributeDto.AttributeValueDto;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute;

public record AttributeDto<T, V extends AttributeValueDto<T>>(
    String type,
    V value
) {

  @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = StringAttributeValueDto.class, name = "String"),
      @JsonSubTypes.Type(value = IntegerAttributeValueDto.class, name = "Integer"),
      @JsonSubTypes.Type(value = LongAttributeValueDto.class, name = "Long"),
      @JsonSubTypes.Type(value = BigDecimalAttributeValueDto.class, name = "BigDecimal"),
  })
  public sealed interface AttributeValueDto<T> {

    T value();

    @SuppressWarnings("unchecked")
    static <T, V extends AttributeValueDto<T>> V of(VariationAttribute.AttributeValue<?> value) {
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
      if (this instanceof AttributeDto.StringAttributeValueDto stringAttributeValue) {
        return (V) VariationAttribute.AttributeValue.stringAttribute(stringAttributeValue.value());
      } else if (this instanceof AttributeDto.IntegerAttributeValueDto integerAttributeValue) {
        return (V) VariationAttribute.AttributeValue.integerAttribute(integerAttributeValue.value());
      } else if (this instanceof AttributeDto.LongAttributeValueDto longAttributeValue) {
        return (V) VariationAttribute.AttributeValue.longAttribute(longAttributeValue.value());
      } else if (this instanceof AttributeDto.BigDecimalAttributeValueDto bigDecimalAttributeValue) {
        return (V) VariationAttribute.AttributeValue.bigDecimalAttribute(bigDecimalAttributeValue.value());
      } else {
        throw new IllegalArgumentException("Cannot map value type %s: %s".formatted(
            this.getClass().getSimpleName(),
            this.value()
        ));
      }
    }

    static StringAttributeValueDto stringAttribute(String value) {
      return new StringAttributeValueDto(value);
    }

    static IntegerAttributeValueDto integerAttribute(Integer value) {
      return new IntegerAttributeValueDto(value);
    }

    static LongAttributeValueDto longAttribute(Long value) {
      return new LongAttributeValueDto(value);
    }

    static BigDecimalAttributeValueDto bigDecimalAttribute(BigDecimal value) {
      return new BigDecimalAttributeValueDto(value);
    }
  }

  public record StringAttributeValueDto(String value) implements AttributeValueDto<String> {

  }

  protected sealed interface NumberAttributeValueDto<T extends Number> extends AttributeValueDto<T> {

  }

  public record IntegerAttributeValueDto(Integer value) implements NumberAttributeValueDto<Integer> {

  }

  public record LongAttributeValueDto(Long value) implements NumberAttributeValueDto<Long> {

  }

  public record BigDecimalAttributeValueDto(BigDecimal value) implements NumberAttributeValueDto<BigDecimal> {

  }
}
