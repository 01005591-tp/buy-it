package pl.edu.pw.ee.pz;

import static lombok.AccessLevel.PRIVATE;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Builder
@RequiredArgsConstructor(access = PRIVATE)
public class HttpApiError {

  private final String code;
  private final String title;
  private final String detail;

  public static BuilderWithCode ofCode(String code) {
    return builder().code(code);
  }

  public static HttpApiError ofCodeOnly(String code) {
    return ofCode(code).noTitle().noDetail();
  }

  public static BuilderWithCode internalServerError() {
    return ofCode(Code.INTERNAL_SERVER_ERROR);
  }

  private static HttpApiErrorBuilder builder() {
    return new HttpApiErrorBuilder();
  }

  public interface BuilderWithCode {

    BuilderWithTitle title(String title);

    default BuilderWithTitle noTitle() {
      return title(StringUtils.EMPTY);
    }
  }

  public interface BuilderWithTitle {

    HttpApiError detail(String detail);

    default HttpApiError noDetail() {
      return detail(StringUtils.EMPTY);
    }
  }

  private static class HttpApiErrorBuilder implements BuilderWithCode, BuilderWithTitle {

    public HttpApiError detail(String detail) {
      this.detail = detail;
      return build();
    }
  }

  public static final class Code {

    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    private Code() {
      throw new UnsupportedOperationException("Cannot instantiate utility class.");
    }
  }
}
