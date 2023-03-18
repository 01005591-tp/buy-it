package pl.edu.pw.ee.pz.sharedkernel.model;

/**
 * Extends {@link Comparable} interface. Adds convenience comparison methods. All the results are based on the
 * {@link Comparable#compareTo(Object)} contract and use this method to perform comparisons.
 *
 * @param <T>
 */
public interface FluentComparable<T> extends Comparable<T> {

  default boolean isGreaterThan(T other) {
    return compareTo(other) > 0;
  }

  default boolean isEqualTo(T other) {
    return compareTo(other) == 0;
  }

  default boolean isLowerThan(T other) {
    return compareTo(other) < 0;
  }

  default boolean isGreaterOrEqualTo(T other) {
    return compareTo(other) >= 0;
  }

  default boolean isLowerOrEqualTo(T other) {
    return compareTo(other) <= 0;
  }
}
