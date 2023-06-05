package pl.edu.pw.ee.pz.sharedkernel.query;

public record ResultPage(
    long size,
    long prevKeySetId,
    long nextKeySetId
) {

  private static final ResultPage EMPTY = new ResultPage(0L, 0L, 0L);
  private static final ResultPage SINGLE = new ResultPage(1L, 0L, 0L);

  public static ResultPage empty() {
    return EMPTY;
  }

  public static ResultPage single() {
    return SINGLE;
  }
}
