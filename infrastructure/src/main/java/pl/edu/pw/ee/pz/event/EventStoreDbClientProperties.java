package pl.edu.pw.ee.pz.event;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "eventstore.db")
public interface EventStoreDbClientProperties {

  String uri();

  String username();

  String password();

  boolean tls();
}
