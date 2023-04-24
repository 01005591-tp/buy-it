package pl.edu.pw.ee.pz.brand;

import io.vertx.mutiny.pgclient.PgPool;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import pl.edu.pw.ee.pz.event.EventStoreRepository;

@ApplicationScoped
public class BrandInfrastructureConfiguration {

  @Produces
  BrandAggregateRepository brandRepository(EventStoreRepository eventStoreRepository) {
    return new BrandAggregateRepository(eventStoreRepository);
  }

  @Produces
  BrandRdbRepository brandRdbRepository(PgPool pgPool) {
    return new BrandRdbRepository(pgPool);
  }
}
