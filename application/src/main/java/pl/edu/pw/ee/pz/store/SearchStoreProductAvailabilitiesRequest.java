package pl.edu.pw.ee.pz.store;

import org.jboss.resteasy.reactive.RestQuery;

class SearchStoreProductAvailabilitiesRequest {

  @RestQuery
  String code;
  @RestQuery
  String brandId;
  @RestQuery
  Long pageSize;
  @RestQuery
  Long keySetItemId;

}
