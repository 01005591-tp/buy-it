package pl.edu.pw.ee.pz.sharedkernel.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = BrandId.class, name = "BrandId"),
    @JsonSubTypes.Type(value = ProductId.class, name = "ProductId"),
    @JsonSubTypes.Type(value = StoreId.class, name = "StoreId"),
})
public interface AggregateId {

  @JsonProperty("value")
  String value();
}
