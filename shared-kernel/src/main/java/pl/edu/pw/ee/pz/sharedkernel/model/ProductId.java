package pl.edu.pw.ee.pz.sharedkernel.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.Accessors;
import pl.edu.pw.ee.pz.sharedkernel.event.AggregateUuidId;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
public final class ProductId extends AggregateUuidId {

  public ProductId(UUID id) {
    super(id);
  }

  @JsonCreator(mode = Mode.PROPERTIES)
  ProductId(@JsonProperty("value") String value) {
    this(UUID.fromString(value));
  }
}
