package pl.edu.pw.ee.pz.sharedkernel.query;

import java.util.List;

public record PageResult<T>(
    ResultPage page,
    long pageCount,
    long currentPage,
    List<T> value
) {

  public static <T> PageResult<T> emptyResult(RequestedPage requestedPage) {
    return new PageResult<>(
        ResultPage.empty(requestedPage),
        0L,
        1L,
        List.of()
    );
  }
}
