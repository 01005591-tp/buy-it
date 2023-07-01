package pl.edu.pw.ee.pz;

import static lombok.AccessLevel.PACKAGE;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.RestResponse;

@RequiredArgsConstructor(access = PACKAGE)
class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

  private final ExceptionCodeMapper exceptionCodeMapper;

  @SuppressWarnings("resource")
  @Override
  public Response toResponse(Throwable throwable) {
    return RestResponse.status(Status.INTERNAL_SERVER_ERROR, toApiError(throwable)).toResponse();
  }

  private HttpApiError toApiError(Throwable throwable) {
    return HttpApiError.internalServerError()
        .noTitle()
        .detail(exceptionCodeMapper.toApiErrorCode(throwable));
  }

  static class ExceptionCodeMapper {

    private static final Pattern STRIP_EXCEPTION_SUFFIX_PATTERN = Pattern.compile("(?<code>.*)(?<suffix>Exception)$");

    String toApiErrorCode(Throwable throwable) {
      var matcher = STRIP_EXCEPTION_SUFFIX_PATTERN.matcher(throwable.getClass().getSimpleName());
      if (!matcher.find()) {
        return throwable.getClass().getSimpleName();
      }
      return matcher.group("code");
    }
  }
}
