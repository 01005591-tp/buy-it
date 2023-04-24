package pl.edu.pw.ee.pz.sharedkernel.event;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.SneakyThrows;

public class JsonEventSerializer implements EventSerializer {

  private final ObjectMapper objectMapper = JsonMapper.builder()
      .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
      .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .addModule(new ParameterNamesModule())
      .addModule(new JavaTimeModule())
      .addModule(new Jdk8Module())
      .build();

  @SneakyThrows
  @Override
  public <ID extends AggregateId, E extends DomainEvent<ID>> byte[] serialize(E event) {
    if (isNull(event)) {
      return new byte[0];
    }
    var serializableEvent = new SerializableEvent<>(
        objectMapper.writeValueAsBytes(event),
        event.getClass()
    );
    return objectMapper.writeValueAsBytes(serializableEvent);
  }

  @SneakyThrows
  @Override
  public <ID extends AggregateId, E extends DomainEvent<ID>> E deserialize(byte[] data) {
    if (isNull(data) || data.length == 0) {
      return null;
    }
    var serializableEvent = objectMapper.readValue(data, SerializableEvent.class);
    @SuppressWarnings("unchecked")
    var type = (Class<E>) serializableEvent.type();
    return objectMapper.readValue(serializableEvent.event(), type);
  }
}
