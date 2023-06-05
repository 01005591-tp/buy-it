package pl.edu.pw.ee.pz.sharedkernel.query;

public record RequestedPage(
    long size,
    long keySetItemId
) {

  private static final RequestedPage SINGLE = new RequestedPage(1L, 0L);

  public static RequestedPage single() {
    return SINGLE;
  }

  public static RequestedPage first(long size) {
    return new RequestedPage(size, 0L);
  }
}
