package pl.edu.pw.ee.pz.sharedkernel.event;

import static java.util.Objects.isNull;

import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.json.JsonSerializer;

@RequiredArgsConstructor
public class JsonEventSerializer implements EventSerializer {

  private final JsonSerializer jsonSerializer;

  @Override
  public <ID extends AggregateId, E extends DomainEvent<ID>> byte[] serialize(E event) {
    if (isNull(event)) {
      return new byte[0];
    }
    var serializableEvent = new SerializableEvent<>(
        jsonSerializer.serializeToBytes(event),
        event.getClass()
    );
    return jsonSerializer.serializeToBytes(serializableEvent);
  }

  @Override
  public <ID extends AggregateId, E extends DomainEvent<ID>> E deserialize(byte[] data) {
    if (isNull(data) || data.length == 0) {
      return null;
    }
    var serializableEvent = jsonSerializer.deserialize(data, SerializableEvent.class);
    @SuppressWarnings("unchecked")
    var type = (Class<E>) serializableEvent.type();
    return (E) jsonSerializer.deserialize(serializableEvent.event(), type);
  }
}
