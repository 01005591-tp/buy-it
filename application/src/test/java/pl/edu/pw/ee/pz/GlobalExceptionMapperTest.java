package pl.edu.pw.ee.pz;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.GlobalExceptionMapper.ExceptionCodeMapper;
import pl.edu.pw.ee.pz.HttpApiError.Code;

class GlobalExceptionMapperTest {

  private final ExceptionCodeMapper exceptionCodeMapper = new ExceptionCodeMapper();
  private final GlobalExceptionMapper subjectUnderTest = new GlobalExceptionMapper(exceptionCodeMapper);

  @Test
  void should_map_throwable_to_response() {
    // given
    var exception = new UnexpectedErrorException();

    // when
    try (var response = subjectUnderTest.toResponse(exception)) {
      // then
      assertThat(response.getStatus()).isEqualTo(Status.INTERNAL_SERVER_ERROR.getStatusCode());
      var httpApiError = response.readEntity(HttpApiError.class);
      assertThat(httpApiError.code()).isEqualTo(Code.INTERNAL_SERVER_ERROR);
      assertThat(httpApiError.title()).isNullOrEmpty();
      assertThat(httpApiError.detail()).isEqualTo("UnexpectedError");
    }
  }

  private static final class UnexpectedErrorException extends RuntimeException {

  }
}