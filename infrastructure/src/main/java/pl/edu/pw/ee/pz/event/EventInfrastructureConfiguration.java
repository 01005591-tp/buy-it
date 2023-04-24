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

@ApplicationScoped
public class EventInfrastructureConfiguration {

  @Produces
  JsonEventSerializer jsonEventSerializer() {
    return new JsonEventSerializer();
  }

  @Produces
  EventStoreDbClient eventStoreDbClient(
      EventSerializer eventSerializer,
      EventStoreDbClientProperties eventStoreDbClientProperties
  ) {
    var uri = URI.create(eventStoreDbClientProperties.uri());
    var settings = EventStoreDBClientSettings.builder()
        .addHost(new Endpoint(uri.getHost(), uri.getPort()))
        .tls(eventStoreDbClientProperties.tls())
        .defaultCredentials(eventStoreDbClientProperties.username(), eventStoreDbClientProperties.password())
        .buildConnectionSettings();
    var client = EventStoreDBClient.create(settings);
    return new EventStoreDbClient(eventSerializer, client);
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
