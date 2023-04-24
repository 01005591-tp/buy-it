package pl.edu.pw.ee.pz.sharedkernel.query;

public record ResultPage(
    long size,
    long nextKeySetId

) {

  public static ResultPage empty(RequestedPage requestedPage) {
    return empty(requestedPage.size());
  }

  public static ResultPage empty(long size) {
    return new ResultPage(size, 0L);
  }

}
