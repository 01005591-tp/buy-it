package pl.edu.pw.ee.pz.product;

import org.jboss.resteasy.reactive.RestQuery;

class SearchProductForManagingRequest {

  @RestQuery
  String code;
  @RestQuery
  String brandId;
  @RestQuery
  Long pageSize;
  @RestQuery
  Long keySetItemId;
}
