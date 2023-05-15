package pl.edu.pw.ee.pz.util;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.testcontainers.containers.GenericContainer;
import pl.edu.pw.ee.pz.util.MinioContainer.MinioCredentials;

@ApplicationScoped
public class CustomResourcesInitializer implements QuarkusTestResourceLifecycleManager {

  public static final MinioCredentials MINIO_CREDENTIALS = new MinioCredentials(
      "test-user",
      "test-password"
  );
  public static final String EVENT_STORE_USERNAME = "admin";
  public static final String EVENT_STORE_PASSWORD = "changeit";

  @Getter
  private MinioContainer minioContainer;
  @Getter
  private EventStoreDbContainer eventStoreDbContainer;

  @Override
  public Map<String, String> start() {
    this.minioContainer = new MinioContainer(MINIO_CREDENTIALS);
    this.eventStoreDbContainer = new EventStoreDbContainer();

    startAndRegisterShutdownHook(minioContainer);
    startAndRegisterShutdownHook(eventStoreDbContainer);

    return Map.ofEntries(
        Map.entry(
            "minio.client.uri",
            "http://%s:%d".formatted(minioContainer.getHost(), minioContainer.getMinioPort())
        ),
        Map.entry("minio.client.access-key", MINIO_CREDENTIALS.username()),
        Map.entry("minio.client.secret-key", MINIO_CREDENTIALS.password()),
        Map.entry(
            "eventstore.db.hosts[0].host",
            "%s".formatted(eventStoreDbContainer.getHost())
        ),
        Map.entry(
            "eventstore.db.hosts[0].port",
            "%d".formatted(eventStoreDbContainer.getHttpPort())
        ),
        Map.entry("eventstore.db.username", EVENT_STORE_USERNAME),
        Map.entry("eventstore.db.password", EVENT_STORE_PASSWORD),
        Map.entry("eventstore.db.tls", "false")
    );
  }

  private <C extends GenericContainer<C>> void startAndRegisterShutdownHook(C container) {
    Runtime.getRuntime().addShutdownHook(new Thread(container::close));
    container.start();
  }

  @Override
  public synchronized void stop() {
    minioContainer.close();
    eventStoreDbContainer.close();
  }
}
