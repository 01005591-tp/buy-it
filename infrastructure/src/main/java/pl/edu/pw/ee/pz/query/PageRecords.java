package pl.edu.pw.ee.pz.query;

import java.util.List;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult;
import pl.edu.pw.ee.pz.sharedkernel.query.RequestedPage;
import pl.edu.pw.ee.pz.sharedkernel.query.ResultPage;

public record PageRecords<T>(
    RequestedPage requestedPage,
    List<PageRecord<T>> records
) {

  public PageResult<T> toResult() {
    if (records.isEmpty()) {
      return PageResult.emptyResult(requestedPage);
    }

    var lastRecord = records.get(records.size() - 1);
    var elementId = lastRecord.elementId();
    var allCount = lastRecord.allCount();
    var pageSize = requestedPage.size();
    var pagesCount = calculatePagesCount(allCount, pageSize);
    var currentPage = allCount / pageSize + 1;
    var resultPage = new ResultPage(elementId, requestedPage.size());
    return new PageResult<>(
        resultPage,
        pagesCount,
        currentPage,
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
