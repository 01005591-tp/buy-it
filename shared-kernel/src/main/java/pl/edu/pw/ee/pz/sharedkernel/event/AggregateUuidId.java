package pl.edu.pw.ee.pz.sharedkernel.event;

import static lombok.AccessLevel.PROTECTED;

import java.util.StringJoiner;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = PROTECTED)
public abstract class AggregateUuidId implements AggregateId {

  @EqualsAndHashCode.Include
  protected final UUID id;

  @Override
  public String value() {
    return id.toString();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
        .add("id=" + id)
        .toString();
  }
}
