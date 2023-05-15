package pl.edu.pw.ee.pz;

import static java.util.Objects.nonNull;

import io.quarkus.runtime.StartupEvent;
import java.sql.DriverManager;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * <p>Workaround for problem with Liquibase and Reactive JDBC connection.</p>
 * <p>
 * <a href="https://github.com/quarkusio/quarkus/issues/14682#issuecomment-828964269">
 * Cannot use Liquibase extension with hibernate-reactive</a>
 * </p>
 */
@ApplicationScoped
@Slf4j
class DbMigration {

  private final String changeLog;
  private final boolean migrateAtStart;
  private final String url;
  private final String username;
  private final String password;

  DbMigration(
      @ConfigProperty(name = "quarkus.liquibase.change-log", defaultValue = "db/changelog/changelog.xml")
      String changeLog,
      @ConfigProperty(name = "quarkus.liquibase.migrate-at-start", defaultValue = "false")
      boolean migrateAtStart,
      @ConfigProperty(name = "quarkus.datasource.reactive.url")
      String reactiveUrl,
      @ConfigProperty(name = "quarkus.datasource.username")
      String username,
      @ConfigProperty(name = "quarkus.datasource.password")
      String password
  ) {
    this.changeLog = changeLog;
    this.migrateAtStart = migrateAtStart;
    this.url = resolveJdbcUrl(reactiveUrl);
    this.username = username;
    this.password = password;
  }


  void onStart(@Observes StartupEvent event) throws LiquibaseException {
    if (!migrateAtStart) {
      return;
    }

    Liquibase liquibase = null;
    try {
      var resourceAccessor = new ClassLoaderResourceAccessor(Thread.currentThread().getContextClassLoader());
      var connection = DriverManager.getConnection(url, username, password);
      var jdbcConnection = new JdbcConnection(connection);
      var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection);
      liquibase = new Liquibase(changeLog, resourceAccessor, database);
      liquibase.update(new Contexts(), new LabelExpression());
    } catch (Exception exception) {
      log.error("Database migration failed", exception);
      throw new RuntimeException(exception);
    } finally {
      if (nonNull(liquibase)) {
        liquibase.close();
      }
    }
  }

  private String resolveJdbcUrl(String url) {
    var pattern = Pattern.compile("^(?<prefix>.+://)(?<url>.*)$");
    var matcher = pattern.matcher(url);
    if (matcher.find()) {
      return "jdbc:postgresql://" + matcher.group("url");
    } else {
      return url;
    }
  }
}
