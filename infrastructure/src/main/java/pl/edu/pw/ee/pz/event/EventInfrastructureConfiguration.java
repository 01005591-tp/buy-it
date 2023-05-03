package pl.edu.pw.ee.pz.event;

import com.eventstore.dbclient.Endpoint;
import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.EventStoreDBClientSettings;
import io.smallrye.reactive.messaging.kafka.KafkaClientService;
import java.net.URI;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import pl.edu.pw.ee.pz.sharedkernel.event.EventSerializer;
import pl.edu.pw.ee.pz.sharedkernel.event.JsonEventSerializer;
import pl.edu.pw.ee.pz.sharedkernel.json.JsonSerializer;

@ApplicationScoped
public class EventInfrastructureConfiguration {

  @Produces
  JsonSerializer jsonSerializer() {
    return new JsonSerializer();
  }

  @Produces
  JsonEventSerializer jsonEventSerializer(JsonSerializer jsonSerializer) {
    return new JsonEventSerializer(jsonSerializer);
  }

  @Produces
  EventStoreDBClient eventStoreDBClient(EventStoreDbClientProperties eventStoreDbClientProperties) {
    var uri = URI.create(eventStoreDbClientProperties.uri());
    var settings = EventStoreDBClientSettings.builder()
        .addHost(new Endpoint(uri.getHost(), uri.getPort()))
        .tls(eventStoreDbClientProperties.tls())
        .defaultCredentials(eventStoreDbClientProperties.username(), eventStoreDbClientProperties.password())
        .buildConnectionSettings();
    return EventStoreDBClient.create(settings);
  }

  @Produces
  EventStoreDbClient eventStoreDbClient(
      EventSerializer eventSerializer,
      EventStoreDBClient eventStoreDBClient
  ) {

    return new EventStoreDbClient(eventSerializer, eventStoreDBClient);
  }

  @Produces
  EventStoreRepository eventStoreRepository(EventStoreDbClient eventStoreDbClient, EventPublisher eventPublisher) {
    return new EventStoreRepository(eventStoreDbClient, eventPublisher);
  }

  @Produces
  KafkaEventPublisher kafkaEventPublisher(
      EventSerializer eventSerializer,
      KafkaClientService kafkaClientService,
      @ConfigProperty(name = "mp.messaging.outgoing.domain-events.topic", defaultValue = "domain-events")
      String topic,
      DomainEventsProperties domainEventsProperties
  ) {
    if (Objects.requireNonNull(topic, "Property \"mp.messaging.outgoing.domain-events-out.topic\" must not be null.")
        .isBlank()) {
      throw new IllegalArgumentException(
          "Property \"mp.messaging.outgoing.domain-events-out.topic\" must not be blank."
      );
    }
    return new KafkaEventPublisher(eventSerializer, kafkaClientService, topic, domainEventsProperties);
  }
}
