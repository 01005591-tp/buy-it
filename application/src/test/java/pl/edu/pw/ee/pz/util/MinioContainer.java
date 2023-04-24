package pl.edu.pw.ee.pz.util;

import static java.util.Objects.nonNull;

import java.time.Duration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.Base58;

public class MinioContainer extends GenericContainer<MinioContainer> {

  private static final String IMAGE = "minio/minio:latest";
  private static final int MINIO_PORT = 9000;
  private static final int MINIO_CONSOLE_PORT = 9001;
  private static final String MINIO_ROOT_USER = "MINIO_ROOT_USER";
  private static final String MINIO_ROOT_PASSWORD = "MINIO_ROOT_PASSWORD";
  private static final String DEFAULT_STORAGE_DIRECTORY = "/data";
  private static final String HEALTH_ENDPOINT = "/minio/health/ready";

  public MinioContainer(MinioCredentials credentials) {
    super(IMAGE);
    withNetworkAliases("minio-" + Base58.randomString(6));
    addExposedPorts(MINIO_PORT, MINIO_CONSOLE_PORT);
    if (nonNull(credentials)) {
      addEnv(MINIO_ROOT_USER, credentials.username());
      addEnv(MINIO_ROOT_PASSWORD, credentials.password());
    }
//    withCommand("server", DEFAULT_STORAGE_DIRECTORY);
    withCommand(
        "server",
        "--address", ":%d".formatted(MINIO_PORT),
        "--console-address", ":%d".formatted(MINIO_CONSOLE_PORT),
        DEFAULT_STORAGE_DIRECTORY
    );
    setWaitStrategy(new HttpWaitStrategy()
        .forPort(MINIO_PORT)
        .forPath(HEALTH_ENDPOINT)
        .withStartupTimeout(Duration.ofMinutes(2L)));
  }

  public int getMinioPort() {
    return getMappedPort(MINIO_PORT);
  }

  record MinioCredentials(String username, String password) {

  }
}
