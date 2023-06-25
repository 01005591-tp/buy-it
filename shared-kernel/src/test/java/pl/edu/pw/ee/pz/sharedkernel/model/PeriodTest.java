package pl.edu.pw.ee.pz.sharedkernel.model;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.control.Option;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.edu.pw.ee.pz.sharedkernel.model.Period.FromAfterToException;
import pl.edu.pw.ee.pz.sharedkernel.model.Period.FromAndToEqualException;
import pl.edu.pw.ee.pz.sharedkernel.model.Period.FromUndefinedException;
import pl.edu.pw.ee.pz.sharedkernel.model.Period.ToUndefinedException;

class PeriodTest {

  @Nested
  class InstantiationSuccess {

    @Test
    void should_instantiate_valid_defined_period_starting_from() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");
      var to = toInstant("2023-06-15T00:00:01Z");

      // when
      var period = Period.builder()
          .from(from)
          .to(to);

      // then
      assertThat(period.from().isDefined()).isTrue();
      assertThat(period.from().get()).isEqualTo(from);
      assertThat(period.to().isDefined()).isTrue();
      assertThat(period.to().get()).isEqualTo(to);
    }

    @Test
    void should_instantiate_valid_defined_period_starting_to() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");
      var to = toInstant("2023-06-15T00:00:01Z");

      // when
      var period = Period.builder()
          .to(to)
          .from(from);

      // then
      assertThat(period.from().isDefined()).isTrue();
      assertThat(period.from().get()).isEqualTo(from);
      assertThat(period.to().isDefined()).isTrue();
      assertThat(period.to().get()).isEqualTo(to);
    }

    @Test
    void should_instantiate_valid_from_undefined_starting_from() {
      // given
      var to = toInstant("2023-06-15T00:00:01Z");

      // when
      var period = Period.builder()
          .fromUndefined()
          .to(to);

      // then
      assertThat(period.from().isEmpty()).isTrue();
      assertThat(period.to().isDefined()).isTrue();
      assertThat(period.to().get()).isEqualTo(to);
    }

    @Test
    void should_instantiate_valid_from_undefined_starting_to() {
      // given
      var to = toInstant("2023-06-15T00:00:01Z");

      // when
      var period = Period.builder()
          .to(to)
          .fromUndefined();

      // then
      assertThat(period.from().isEmpty()).isTrue();
      assertThat(period.to().isDefined()).isTrue();
      assertThat(period.to().get()).isEqualTo(to);
    }

    @Test
    void should_instantiate_valid_to_undefined_starting_from() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");

      // when
      var period = Period.builder()
          .from(from)
          .toUndefined();

      // then
      assertThat(period.from().isDefined()).isTrue();
      assertThat(period.from().get()).isEqualTo(from);
      assertThat(period.to().isEmpty()).isTrue();
    }

    @Test
    void should_instantiate_valid_to_undefined_starting_to() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");

      // when
      var period = Period.builder()
          .toUndefined()
          .from(from);

      // then
      assertThat(period.from().isDefined()).isTrue();
      assertThat(period.from().get()).isEqualTo(from);
      assertThat(period.to().isEmpty()).isTrue();
    }

    @Test
    void should_instantiate_valid_both_undefined_starting_from() {
      // when
      var period = Period.builder()
          .fromUndefined()
          .toUndefined();

      // then
      assertThat(period.from().isEmpty()).isTrue();
      assertThat(period.to().isEmpty()).isTrue();
    }

    @Test
    void should_instantiate_valid_both_undefined_starting_to() {
      // when
      var period = Period.builder()
          .toUndefined()
          .fromUndefined();

      // then
      assertThat(period.from().isEmpty()).isTrue();
      assertThat(period.to().isEmpty()).isTrue();
    }

    @Test
    void undefined_should_be_the_same_instance() {
      // when
      var first = Period.builder()
          .fromUndefined()
          .toUndefined();
      var second = Period.builder()
          .toUndefined()
          .fromUndefined();

      // then
      assertThat(first == second).isTrue();
    }
  }

  @Nested
  class InstantiationFailure {

    @Test
    void should_fail_instantiation_from_after_to_starting_from() {
      // given
      var from = toInstant("2023-06-15T00:00:01Z");
      var to = toInstant("2023-06-15T00:00:00Z");

      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .from(from)
              .to(to)
      );

      // then
      throwableAssert.isInstanceOf(FromAfterToException.class);
    }

    @Test
    void should_fail_instantiation_from_after_to_starting_to() {
      // given
      var from = toInstant("2023-06-15T00:00:01Z");
      var to = toInstant("2023-06-15T00:00:00Z");

      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .to(to)
              .from(from)
      );

      // then
      throwableAssert.isInstanceOf(FromAfterToException.class);
    }

    @Test
    void should_fail_instantiation_from_and_to_equal_starting_from() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");
      var to = toInstant("2023-06-15T00:00:00Z");

      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .from(from)
              .to(to)
      );

      // then
      throwableAssert.isInstanceOf(FromAndToEqualException.class);
    }

    @Test
    void should_fail_instantiation_from_and_to_equal_starting_to() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");
      var to = toInstant("2023-06-15T00:00:00Z");

      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .to(to)
              .from(from)
      );

      // then
      throwableAssert.isInstanceOf(FromAndToEqualException.class);
    }

    @Test
    void should_fail_instantiation_from_undefined_starting_from() {
      // given
      var to = toInstant("2023-06-15T00:00:00Z");

      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .from(null)
              .to(to)
      );

      // then
      throwableAssert.isInstanceOf(FromUndefinedException.class);
    }

    @Test
    void should_fail_instantiation_from_undefined_starting_to() {
      // given
      var to = toInstant("2023-06-15T00:00:00Z");

      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .to(to)
              .from(null)
      );

      // then
      throwableAssert.isInstanceOf(FromUndefinedException.class);
    }

    @Test
    void should_fail_instantiation_from_undefined_with_to_undefined_starting_from() {
      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .from(null)
              .toUndefined()
      );

      // then
      throwableAssert.isInstanceOf(FromUndefinedException.class);
    }

    @Test
    void should_fail_instantiation_from_undefined_with_to_undefined_starting_to() {
      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .toUndefined()
              .from(null)
      );

      // then
      throwableAssert.isInstanceOf(FromUndefinedException.class);
    }

    @Test
    void should_fail_instantiation_to_undefined_starting_from() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");

      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .from(from)
              .to(null)
      );

      // then
      throwableAssert.isInstanceOf(ToUndefinedException.class);
    }

    @Test
    void should_fail_instantiation_to_undefined_starting_to() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");

      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .to(null)
              .from(from)
      );

      // then
      throwableAssert.isInstanceOf(ToUndefinedException.class);
    }

    @Test
    void should_fail_instantiation_to_undefined_with_from_undefined_starting_from() {
      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .fromUndefined()
              .to(null)
      );

      // then
      throwableAssert.isInstanceOf(ToUndefinedException.class);
    }

    @Test
    void should_fail_instantiation_to_undefined_with_from_undefined_starting_to() {
      // when
      var throwableAssert = Assertions.assertThatCode(() ->
          Period.builder()
              .to(null)
              .fromUndefined()
      );

      // then
      throwableAssert.isInstanceOf(ToUndefinedException.class);
    }
  }

  @Nested
  class Modification {

    @Test
    void should_modify_to_value() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");
      var to = toInstant("2023-06-15T00:00:01Z");
      var toChanged = toInstant("2023-06-15T00:00:05Z");
      var period = Period.builder()
          .from(from)
          .to(to);

      // when
      var periodAfterChange = period.withTo()
          .to(toChanged);

      // then
      assertThat(periodAfterChange.from().isDefined()).isTrue();
      assertThat(periodAfterChange.from().get()).isEqualTo(from);
      assertThat(periodAfterChange.to().isDefined()).isTrue();
      assertThat(periodAfterChange.to().get()).isEqualTo(toChanged);
    }

    @Test
    void should_modify_to_value_to_undefined() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");
      var to = toInstant("2023-06-15T00:00:01Z");
      var toChanged = toInstant("2023-06-15T00:00:05Z");
      var period = Period.builder()
          .from(from)
          .to(to);

      // when
      var periodAfterChange = period.withTo()
          .toUndefined();

      // then
      assertThat(periodAfterChange.from().isDefined()).isTrue();
      assertThat(periodAfterChange.from().get()).isEqualTo(from);
      assertThat(periodAfterChange.to().isEmpty()).isTrue();
    }

    @Test
    void should_modify_from_value() {
      // given
      var from = toInstant("2023-06-15T00:00:01Z");
      var to = toInstant("2023-06-15T00:00:02Z");
      var fromChanged = toInstant("2023-06-15T00:00:00Z");
      var period = Period.builder()
          .from(from)
          .to(to);

      // when
      var periodAfterChange = period.withFrom()
          .from(fromChanged);

      // then
      assertThat(periodAfterChange.from().isDefined()).isTrue();
      assertThat(periodAfterChange.from().get()).isEqualTo(fromChanged);
      assertThat(periodAfterChange.to().isDefined()).isTrue();
      assertThat(periodAfterChange.to().get()).isEqualTo(to);
    }

    @Test
    void should_modify_from_value_to_undefined() {
      // given
      var from = toInstant("2023-06-15T00:00:00Z");
      var to = toInstant("2023-06-15T00:00:01Z");
      var period = Period.builder()
          .from(from)
          .to(to);

      // when
      var periodAfterChange = period.withFrom()
          .fromUndefined();

      // then
      assertThat(periodAfterChange.from().isEmpty()).isTrue();
      assertThat(periodAfterChange.to().isDefined()).isTrue();
      assertThat(periodAfterChange.to().get()).isEqualTo(to);
    }
  }

  @Nested
  class Overlapping {

    @CsvSource(delimiter = '|', nullValues = "null", value = {
        //        f1     f2
        //         |_____|
        // |_____|
        // s1   s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-14T23:59:58Z | 2023-06-14T23:59:59Z | false ",
        //       f1      f2
        //        |______|
        // |______|
        // s1     s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-14T23:59:58Z | 2023-06-15T00:00:00Z | false ",
        //     f1         f2
        //      |_________|
        // |_________|
        // s1        s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-14T23:59:58Z | 2023-06-15T00:00:01Z | true  ",
        //     f1     f2
        //      |_____|
        // |__________|
        // s1        s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-14T23:59:58Z | 2023-06-15T00:00:03Z | true  ",
        //  f1   f2
        //   |____|
        // |_________|
        // s1       s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-14T23:59:58Z | 2023-06-15T00:00:04Z | true  ",
        // f1     f2
        // |______|
        // |____|
        // s1   s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-15T00:00:00Z | 2023-06-15T00:00:01Z | true  ",
        // f1     f2
        // |______|
        // |______|
        // s1     s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | true  ",
        // f1     f2
        // |______|
        // |________|
        // s1       s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-15T00:00:00Z | 2023-06-15T00:00:04Z | true  ",
        // f1       f2
        // |_________|
        //   |_____|
        //   s1    s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-15T00:00:01Z | 2023-06-15T00:00:02Z | true  ",
        // f1     f2
        // |_______|
        //   |_____|
        //   s1    s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-15T00:00:01Z | 2023-06-15T00:00:03Z | true  ",
        // f1     f2
        // |_______|
        //   |_______|
        //   s1      s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-15T00:00:01Z | 2023-06-15T00:00:04Z | true  ",
        // f1  f2
        // |____|
        //      |____|
        //      s1   s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-15T00:00:03Z | 2023-06-15T00:00:04Z | false ",
        // f1  f2
        // |____|
        //        |____|
        //        s1   s2
        " 2023-06-15T00:00:00Z | 2023-06-15T00:00:03Z | 2023-06-15T00:00:04Z | 2023-06-15T00:00:05Z | false ",
        // f1      f2
        // ∞  _____|
        //   |____|
        //   s1   s2
        " null                 | 2023-06-15T00:00:03Z | 2023-06-14T23:59:59Z | 2023-06-15T00:00:01Z | true  ",
        // f1     f2
        // ∞  ____|
        //   |____|
        //   s1   s2
        " null                 | 2023-06-15T00:00:03Z | 2023-06-14T23:59:59Z | 2023-06-15T00:00:03Z | true  ",
        // f1     f2
        // ∞  ____|
        //   |______|
        //   s1     s2
        " null                 | 2023-06-15T00:00:03Z | 2023-06-14T23:59:59Z | 2023-06-15T00:00:04Z | true  ",
        // f1   f2
        // ∞  __|
        //      |____|
        //     s1    s2
        " null                 | 2023-06-15T00:00:03Z | 2023-06-15T00:00:03Z | 2023-06-15T00:00:04Z | false ",
        // f1   f2
        // ∞  __|
        //        |____|
        //       s1    s2
        " null                 | 2023-06-15T00:00:03Z | 2023-06-15T00:00:04Z | 2023-06-15T00:00:05Z | false ",
        // f1     f2
        // ∞  ____|
        // ∞  ___|
        // s1    s2
        " null                 | 2023-06-15T00:00:03Z | null                 | 2023-06-15T00:00:02Z | true  ",
        // f1     f2
        // ∞  ____|
        // ∞  ____|
        // s1     s2
        " null                 | 2023-06-15T00:00:03Z | null                 | 2023-06-15T00:00:03Z | true  ",
        // f1     f2
        // ∞  ____|
        // ∞  ______|
        // s1     s2
        " null                 | 2023-06-15T00:00:03Z | null                 | 2023-06-15T00:00:04Z | true  ",
        //       f1       f2
        //       |_______  ∞
        // |___|
        // s1  s2
        " 2023-06-15T00:00:00Z | null                 | 2023-06-14T23:59:58Z | 2023-06-14T23:59:59Z | false ",
        //     f1       f2
        //     |_______  ∞
        // |___|
        // s1  s2
        " 2023-06-15T00:00:00Z | null                 | 2023-06-14T23:59:58Z | 2023-06-15T00:00:00Z | false ",
        //    f1       f2
        //    |_______  ∞
        // |____|
        // s1   s2
        " 2023-06-15T00:00:00Z | null                 | 2023-06-14T23:59:58Z | 2023-06-15T00:00:01Z | true  ",
        // f1       f2
        // |_______  ∞
        // |___|
        // s1  s2
        " 2023-06-15T00:00:00Z | null                 | 2023-06-15T00:00:00Z | 2023-06-15T00:00:01Z | true  ",
        // f1       f2
        // |_______  ∞
        // |_______  ∞
        // s1       s2
        " 2023-06-15T00:00:00Z | null                 | 2023-06-15T00:00:00Z | null                 | true  ",
        // f1       f2
        // ∞  _____  ∞
        //   |_____|
        //  s1     s2
        " null                 | null                 | 2023-06-15T00:00:00Z | 2023-06-15T00:00:01Z | true  ",
        // f1       f2
        // ∞  _____  ∞
        // ∞  _____|
        // s1      s2
        " null                 | null                 | null                 | 2023-06-15T00:00:01Z | true  ",
        // f1       f2
        // ∞  _____  ∞
        //   |_____  ∞
        //  s1      s2
        " null                 | null                 | 2023-06-15T00:00:00Z | null                 | true  ",
        // f1       f2
        // ∞  _____  ∞
        // ∞  _____  ∞
        // s1       s2
        " null                 | null                 | null                 | null                 | true  ",
    })
    @ParameterizedTest(name = "[{index}] ({0} - {1}) ({2} - {3}): {4}")
    void should_resolve_overlapping_in_both_ways(
        String firstFrom,
        String firstTo,
        String secondFrom,
        String secondTo,
        boolean overlapping
    ) {
      // given
      var firstPeriod = toPeriod(firstFrom, firstTo);
      var secondPeriod = toPeriod(secondFrom, secondTo);

      // when
      var secondOverlapsFirst = firstPeriod.overlaps(secondPeriod);
      var firstOverlapsSecond = secondPeriod.overlaps(firstPeriod);

      // then
      assertThat(secondOverlapsFirst).isEqualTo(overlapping);
      assertThat(firstOverlapsSecond).isEqualTo(overlapping);
    }
  }

  private static Period toPeriod(String maybeFrom, String maybeTo) {
    var from = tryParseToInstant(maybeFrom);
    var to = tryParseToInstant(maybeTo);

    if (from.isEmpty() && to.isEmpty()) {
      return Period.builder().fromUndefined().toUndefined();
    }
    return from.map(it -> Period.builder().from(it))
        .map(it -> to.fold(it::toUndefined, it::to))
        .orElse(() -> to.map(it -> Period.builder().to(it))
            .map(it -> from.fold(it::fromUndefined, it::from))
        )
        .getOrElseThrow(NoSuchElementException::new);
  }

  private static Option<Instant> tryParseToInstant(String utcDateTime) {
    return Option.of(utcDateTime)
        .map(OffsetDateTime::parse)
        .map(OffsetDateTime::toInstant);
  }

  private static Instant toInstant(String utcDateTime) {
    return OffsetDateTime.parse(utcDateTime).toInstant();
  }
}