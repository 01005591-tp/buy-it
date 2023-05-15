package pl.edu.pw.ee.pz.event;

import io.smallrye.config.ConfigMapping;
import java.util.List;

@ConfigMapping(prefix = "eventstore.db")
public interface EventStoreDbClientProperties {

  List<EventStoreDbClientHost> hosts();

  String username();

  String password();

  boolean tls();

  interface EventStoreDbClientHost {

    String host();

    Integer port();
  }
}
