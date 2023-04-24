package pl.edu.pw.ee.pz.event;

import io.smallrye.config.ConfigMapping;
import java.time.Duration;

@ConfigMapping(prefix = "domain-events")
public interface DomainEventsProperties {

  Duration publishTimeout();

  Duration backoffTimeout();

  int maxRetries();
}
