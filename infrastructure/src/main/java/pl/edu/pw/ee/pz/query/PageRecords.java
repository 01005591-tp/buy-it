package pl.edu.pw.ee.pz.query;

import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;
import pl.edu.pw.ee.pz.sharedkernel.query.RequestedPage;
import pl.edu.pw.ee.pz.sharedkernel.query.ResultPage;

public record PageRecords<T>(
    RequestedPage requestedPage,
    List<PageRecord<T>> records
) {

  public static <T> PageRecords<T> empty(RequestedPage requestedPage) {
    return new PageRecords<>(requestedPage, List.of());
  }

  public PageResult<T> toResult() {
    if (records.isEmpty()) {
      return PageResult.empty();
    } else if (records.size() == 1) {
      return PageResult.single(records.get(0).value());
    }

    var firstRecord = records.get(0);
    var allCount = firstRecord.allCount();
    var pageSize = requestedPage.size();
    var pagesCount = calculatePagesCount(allCount, pageSize);
    var currentPageSize = Math.min(requestedPage.size(), records.size());
    var greatestElementId = records.stream().mapToLong(PageRecord::elementId).max().orElse(0L);
    var resultPage = new ResultPage(currentPageSize, requestedPage.keySetItemId(), greatestElementId);
    return PageResult.multi(
        resultPage,
        pagesCount,
        allCount,
        unwrapRecords()
    );
  }

  private long calculatePagesCount(long allCount, long pageSize) {
    return (long) Math.ceil((double) allCount / pageSize);
  }

  private List<T> unwrapRecords() {
    return records.stream()
        .map(PageRecord::value)
        .toList();
  }
}
