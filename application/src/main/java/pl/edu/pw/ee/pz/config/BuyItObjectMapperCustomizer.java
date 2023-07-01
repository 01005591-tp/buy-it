package pl.edu.pw.ee.pz.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.quarkus.jackson.ObjectMapperCustomizer;
import io.vavr.jackson.datatype.VavrModule;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class BuyItObjectMapperCustomizer implements ObjectMapperCustomizer {

  @Override
  public void customize(ObjectMapper objectMapper) {
    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
    objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.registerModule(new ParameterNamesModule());
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new Jdk8Module());
    objectMapper.registerModule(new VavrModule());
  }
}
