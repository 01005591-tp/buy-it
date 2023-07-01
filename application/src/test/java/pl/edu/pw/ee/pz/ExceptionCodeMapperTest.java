package pl.edu.pw.ee.pz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.edu.pw.ee.pz.GlobalExceptionMapper.ExceptionCodeMapper;

class ExceptionCodeMapperTest {

  private final ExceptionCodeMapper subjectUnderTest = new ExceptionCodeMapper();

  private static List<Arguments> exceptionsWithSuffixParams() {
    return List.of(
        arguments("SomeSimpleException", new SomeSimpleException(), "SomeSimple"),
        arguments("DoubleExceptionException", new DoubleExceptionException(), "DoubleException"),
        arguments(
            "ExceptionStartingAndEndingWithException", new ExceptionStartingAndEndingWithException(),
            "ExceptionStartingAndEndingWith"
        ),
        arguments(
            "ExceptionWithoutExceptionSuffix", new ExceptionWithoutExceptionSuffix(),
            "ExceptionWithoutExceptionSuffix"
        ),
        arguments("SimpleThrowable", new SimpleThrowable(), "SimpleThrowable")
    );
  }

  @MethodSource("exceptionsWithSuffixParams")
  @ParameterizedTest(name = "[{index}] exception = {0}, expectedCode = {2}")
  void should_resolve_code_stripping_the_exception_suffix(
      @SuppressWarnings("unused") String exceptionName,
      Throwable exception,
      String expectedCode
  ) {
    // when
    var code = subjectUnderTest.toApiErrorCode(exception);

    // then
    assertThat(code).isEqualTo(expectedCode);
  }

  private static final class SomeSimpleException extends RuntimeException {

  }

  private static final class DoubleExceptionException extends RuntimeException {

  }

  private static final class ExceptionStartingAndEndingWithException extends RuntimeException {

  }

  private static final class ExceptionWithoutExceptionSuffix extends RuntimeException {

  }

  private static final class SimpleThrowable extends RuntimeException {

  }

}