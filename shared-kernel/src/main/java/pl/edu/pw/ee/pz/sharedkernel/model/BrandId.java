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
public class BrandId extends AggregateUuidId {

  public BrandId(UUID id) {
    super(id);
  }

  @JsonCreator(mode = Mode.PROPERTIES)
  BrandId(@JsonProperty("value") String value) {
    this(UUID.fromString(value));
  }
}
