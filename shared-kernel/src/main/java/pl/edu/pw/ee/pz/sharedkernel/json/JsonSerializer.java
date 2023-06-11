package pl.edu.pw.ee.pz.sharedkernel.json;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.vavr.jackson.datatype.VavrModule;
import pl.edu.pw.ee.pz.sharedkernel.function.ExceptionUtil;

public class JsonSerializer {

  private final ObjectMapper objectMapper = JsonMapper.builder()
      .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
      .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .addModule(new ParameterNamesModule())
      .addModule(new JavaTimeModule())
      .addModule(new Jdk8Module())
      .addModule(new VavrModule())
      .build();

  public String serialize(Object object) {
    if (isNull(object)) {
      return null;
    }
    return ExceptionUtil.sneakyThrow(() -> objectMapper.writeValueAsString(object));
  }

  public <T> T deserialize(String data, Class<T> type) {
    if (isNull(data) || data.isBlank()) {
      return null;
    }
    return ExceptionUtil.sneakyThrow(() -> objectMapper.readValue(data, type));
  }

  public byte[] serializeToBytes(Object object) {
    if (isNull(object)) {
      return new byte[0];
    }
    return ExceptionUtil.sneakyThrow(() -> objectMapper.writeValueAsBytes(object));
  }

  public <T> T deserialize(byte[] data, Class<T> type) {
    if (isNull(data) || data.length == 0) {
      return null;
    }
    return ExceptionUtil.sneakyThrow(() -> objectMapper.readValue(data, type));
  }

  public <T> T convertValue(Object from, Class<T> type) {
    return ExceptionUtil.sneakyThrow(() -> objectMapper.convertValue(from, type));
  }

  public <T> T treeToValue(TreeNode node, Class<T> type) {
    return ExceptionUtil.sneakyThrow(() -> objectMapper.treeToValue(node, type));
  }
}
