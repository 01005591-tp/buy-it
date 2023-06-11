package pl.edu.pw.ee.pz.sharedkernel.model;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@RequiredArgsConstructor(access = PRIVATE)
public final class Pieces implements FluentComparable<Pieces> {

  private static final Pieces NONE = new Pieces(0L);
  @JsonProperty
  private final Long value;

  @JsonCreator(mode = Mode.PROPERTIES)
  public static Pieces of(Long value) {
    if (isNull(value)) {
      return NONE;
    } else if (value < 0) {
      throw InvalidPiecesCountException.negativeCount(value);
    } else {
      return new Pieces(value);
    }
  }

  public Pieces add(Pieces augend) {
    return new Pieces(value + augend.value());
  }

  public Pieces subtract(Pieces subtrahend) {
    return new Pieces(value - subtrahend.value());
  }

  public boolean isNone() {
    return value == 0;
  }

  @Override
  public int compareTo(Pieces other) {
    return Long.compare(this.value, other.value());
  }

  public static Pieces none() {
    return NONE;
  }

  public static class InvalidPiecesCountException extends RuntimeException {

    @Getter
    @Accessors(fluent = true)
    private final Long count;

    private InvalidPiecesCountException(Long count, String message) {
      super(message);
      this.count = count;
    }

    public static InvalidPiecesCountException negativeCount(Long count) {
      return new InvalidPiecesCountException(
          count,
          "Pieces count cannot be negative. Requested count was %d".formatted(count)
      );
    }
  }
}
