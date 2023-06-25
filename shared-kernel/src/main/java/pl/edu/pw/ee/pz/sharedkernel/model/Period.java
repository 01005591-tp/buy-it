package pl.edu.pw.ee.pz.sharedkernel.model;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

import io.vavr.control.Option;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@RequiredArgsConstructor(access = PRIVATE)
public final class Period {

  private static final Period UNDEFINED = new Period(Option.none(), Option.none());

  private final Option<Instant> from;
  private final Option<Instant> to;

  public boolean overlaps(Period other) {
    return firstOverlaps(this, other) || firstOverlaps(other, this);
  }

  public PeriodBuilderWithFrom withTo() {
    return new PeriodBuilderWithFrom(from);
  }

  public PeriodBuilderWithTo withFrom() {
    return new PeriodBuilderWithTo(to);
  }

  private static boolean firstOverlaps(Period first, Period second) {
    return isBeforeOrEqualTo(first.from(), second.from())
        && (
        first.from().isEmpty() && second.from().isEmpty()
            || isAfter(first.to(), second.from())
    );
  }

  private static boolean isBeforeOrEqualTo(Option<Instant> maybeFirst, Option<Instant> maybeSecond) {
    return maybeFirst.isEmpty() || maybeSecond.isEmpty()
        || maybeFirst.filter(first ->
            maybeSecond.filter(second -> isBeforeOrEqualTo(first, second)).isDefined()
        )
        .isDefined();
  }

  private static boolean isBeforeOrEqualTo(Instant first, Instant second) {
    return first.isBefore(second) || first.equals(second);
  }

  private static boolean isAfter(Option<Instant> maybeFirst, Option<Instant> maybeSecond) {
    return maybeFirst.isEmpty()
        || maybeFirst.filter(first ->
            maybeSecond.filter(first::isAfter).isDefined()
        )
        .isDefined();
  }

  public static PeriodBuilderEmpty builder() {
    return PeriodBuilderEmpty.INSTANCE;
  }

  @NoArgsConstructor(access = PRIVATE)
  public static class PeriodBuilderEmpty {

    private static final PeriodBuilderEmpty INSTANCE = new PeriodBuilderEmpty();

    public PeriodBuilderWithFrom from(Instant from) {
      if (isNull(from)) {
        throw new FromUndefinedException();
      }
      return new PeriodBuilderWithFrom(Option.of(from));
    }

    public PeriodBuilderWithFrom fromUndefined() {
      return PeriodBuilderWithFrom.FROM_UNDEFINED;
    }

    public PeriodBuilderWithTo to(Instant to) {
      if (isNull(to)) {
        throw new ToUndefinedException();
      }
      return new PeriodBuilderWithTo(Option.of(to));
    }

    public PeriodBuilderWithTo toUndefined() {
      return PeriodBuilderWithTo.TO_UNDEFINED;
    }
  }

  @RequiredArgsConstructor(access = PRIVATE)
  public static class PeriodBuilderWithFrom {

    private static final PeriodBuilderWithFrom FROM_UNDEFINED = new PeriodBuilderWithFrom(Option.none());

    private final Option<Instant> from;

    public Period to(Instant to) {
      if (isNull(to)) {
        throw new ToUndefinedException();
      }
      if (from.filter(to::equals).isDefined()) {
        throw new FromAndToEqualException(to);
      }
      var maybeTo = Option.of(to);
      if (from.filter(it -> it.isAfter(to)).isDefined()) {
        throw new FromAfterToException(
            from,
            maybeTo
        );
      }
      return new Period(from, maybeTo);
    }

    public Period toUndefined() {
      return isFromUndefined()
          ? Period.UNDEFINED
          : new Period(from, Option.none());
    }

    private boolean isFromUndefined() {
      return from.isEmpty();
    }
  }

  @RequiredArgsConstructor(access = PRIVATE)
  public static class PeriodBuilderWithTo {

    private static final PeriodBuilderWithTo TO_UNDEFINED = new PeriodBuilderWithTo(Option.none());
    private final Option<Instant> to;

    public Period from(Instant from) {
      if (isNull(from)) {
        throw new FromUndefinedException();
      }
      if (to.filter(from::equals).isDefined()) {
        throw new FromAndToEqualException(from);
      }
      var maybeFrom = Option.of(from);
      if (to.filter(it -> it.isBefore(from)).isDefined()) {
        throw new FromAfterToException(
            maybeFrom,
            to
        );
      }
      return new Period(maybeFrom, to);
    }

    public Period fromUndefined() {
      return isToUndefined()
          ? Period.UNDEFINED
          : new Period(Option.none(), to);
    }

    private boolean isToUndefined() {
      return to.isEmpty();
    }
  }

  public static class InvalidPeriodException extends RuntimeException {

    protected InvalidPeriodException(String message) {
      super(message);
    }
  }

  public static class FromAndToEqualException extends InvalidPeriodException {

    private FromAndToEqualException(Instant value) {
      super("Parameters \"from\" and \"to\" cannot be equal. Got %s".formatted(value.toString()));
    }
  }

  @Getter
  @Accessors(fluent = true)
  public static class FromAfterToException extends InvalidPeriodException {

    private final Option<Instant> from;
    private final Option<Instant> to;

    private FromAfterToException(Option<Instant> from, Option<Instant> to) {
      super("From %s cannot be after to %s".formatted(
          from.map(Instant::toString).getOrElse("-∞"),
          to.map(Instant::toString).getOrElse("+∞")
      ));
      this.from = from;
      this.to = to;
    }
  }

  public static class ToUndefinedException extends InvalidPeriodException {

    private ToUndefinedException() {
      super("Parameter \"to\" is required. Use \"toUndefined()\" method if \"to\" should be undefined.");
    }
  }

  public static class FromUndefinedException extends InvalidPeriodException {

    private FromUndefinedException() {
      super("Parameter \"from\" is required. Use \"fromUndefined()\" method if \"from\" should be undefined.");
    }
  }
}
