package pl.edu.pw.ee.pz.sharedkernel.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult.EmptyPageResult;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult.MultiPageResult;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult.SinglePageResult;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = EmptyPageResult.class, name = "Empty"),
    @JsonSubTypes.Type(value = SinglePageResult.class, name = "Single"),
    @JsonSubTypes.Type(value = MultiPageResult.class, name = "Multi"),
})
public sealed interface PageResult<T> {

  @JsonProperty
  default ResultPage page() {
    return ResultPage.empty();
  }

  @JsonProperty
  default long pageCount() {
    return 0L;
  }

  @JsonProperty
  default long itemsCount() {
    return 0L;
  }

  @JsonIgnore
  default boolean isEmpty() {
    return this instanceof EmptyPageResult<T>;
  }

  @JsonIgnore
  default boolean isSingle() {
    return this instanceof SinglePageResult<T>;
  }

  @JsonIgnore
  default boolean isMulti() {
    return this instanceof MultiPageResult<T>;
  }

  default <V> V fold(
      Supplier<V> onEmpty,
      Function<? super T, ? extends V> onSingle,
      Function<? super List<T>, ? extends V> onMulti
  ) {
    if (this instanceof EmptyPageResult<T> empty) {
      return onEmpty.get();
    } else if (this instanceof SinglePageResult<T> single) {
      return onSingle.apply(single.value());
    } else if (this instanceof MultiPageResult<T> multi) {
      return onMulti.apply(multi.value());
    } else {
      throw new UnsupportedOperationException(
          "Cannot fold %s value".formatted(this.getClass().getSimpleName())
      );
    }
  }

  default <V> V transform(
      Function<? super EmptyPageResult<T>, ? extends V> onEmpty,
      Function<? super SinglePageResult<T>, ? extends V> onSingle,
      Function<? super MultiPageResult<T>, ? extends V> onMulti
  ) {
    if (this instanceof EmptyPageResult<T> empty) {
      return onEmpty.apply(empty);
    } else if (this instanceof SinglePageResult<T> single) {
      return onSingle.apply(single);
    } else if (this instanceof MultiPageResult<T> multi) {
      return onMulti.apply(multi);
    } else {
      throw new UnsupportedOperationException(
          "Cannot fold %s value".formatted(this.getClass().getSimpleName())
      );
    }
  }

  default <V> PageResult<V> map(Function<? super T, ? extends V> mapper) {
    if (this instanceof EmptyPageResult<T> empty) {
      @SuppressWarnings("unchecked")
      var typedResult = (EmptyPageResult<V>) empty;
      return typedResult;
    } else if (this instanceof SinglePageResult<T> single) {
      return PageResult.single(mapper.apply(single.value()));
    } else if (this instanceof MultiPageResult<T> multi) {
      return PageResult.multi(
          multi.page(),
          multi.pageCount(),
          multi.itemsCount(),
          multi.value().stream()
              .map(mapper)
              .collect(Collectors.toUnmodifiableList())
      );
    } else {
      throw new UnsupportedOperationException(
          "Cannot map %s value".formatted(this.getClass().getSimpleName())
      );
    }
  }

  static <T> EmptyPageResult<T> empty() {
    return EmptyPageResult.instance();
  }

  static <T> SinglePageResult<T> single(T value) {
    return new SinglePageResult<>(value);
  }

  static <T> MultiPageResult<T> multi(
      ResultPage resultPage,
      long pageCount,
      long itemsCount,
      List<T> value
  ) {
    return new MultiPageResult<>(
        resultPage,
        pageCount,
        itemsCount,
        value
    );
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record MultiPageResult<T>(
      ResultPage page,
      long pageCount,
      long itemsCount,
      @JsonProperty
      List<T> value
  ) implements PageResult<T> {

  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  final class SinglePageResult<T> implements PageResult<T> {


    @JsonProperty
    @Getter
    @Accessors(fluent = true)
    private final T value;

    @JsonCreator(mode = Mode.PROPERTIES)
    private SinglePageResult(
        @JsonProperty("value") T value
    ) {
      this.value = value;
    }

    @Override
    public ResultPage page() {
      return ResultPage.single();
    }

    @Override
    public long pageCount() {
      return 1L;
    }

    @Override
    public long itemsCount() {
      return 1L;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  final class EmptyPageResult<T> implements PageResult<T> {

    private static final EmptyPageResult<?> INSTANCE = new EmptyPageResult<>();

    private EmptyPageResult() {
    }

    private static <T> EmptyPageResult<T> instance() {
      @SuppressWarnings("unchecked")
      EmptyPageResult<T> typedInstance = (EmptyPageResult<T>) INSTANCE;
      return typedInstance;
    }
  }
}
