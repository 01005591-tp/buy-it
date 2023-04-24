package pl.edu.pw.ee.pz.util;

import java.time.Duration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.Base58;

public class EventStoreDbContainer extends GenericContainer<EventStoreDbContainer> {

  private static final String IMAGE = "eventstore/eventstore:latest";
  private static final int EXT_TCP_PORT = 1113;
  private static final int HTTP_PORT = 2113;
  private static final String HEALTH_ENDPOINT = "/web/index.html";

  public EventStoreDbContainer() {
    super(IMAGE);
    withNetworkAliases("eventstore-" + Base58.randomString(6));
    addExposedPorts(EXT_TCP_PORT, HTTP_PORT);
    addEnv("EVENTSTORE_CLUSTER_SIZE", "1");
    addEnv("EVENTSTORE_RUN_PROJECTIONS", "All");
    addEnv("EVENTSTORE_START_STANDARD_PROJECTIONS", "true");
    addEnv("EVENTSTORE_EXT_TCP_PORT", "1113");
    addEnv("EVENTSTORE_HTTP_PORT", "2113");
    addEnv("EVENTSTORE_INSECURE", "true");
    addEnv("EVENTSTORE_ENABLE_EXTERNAL_TCP", "true");
    addEnv("EVENTSTORE_ENABLE_ATOM_PUB_OVER_HTTP", "true");
    setWaitStrategy(new HttpWaitStrategy()
        .forPort(HTTP_PORT)
        .forPath(HEALTH_ENDPOINT)
        .withStartupTimeout(Duration.ofMinutes(2L))
    );
  }

  public int getHttpPort() {
    return getMappedPort(HTTP_PORT);
  }
}
