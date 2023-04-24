package pl.edu.pw.ee.pz.brand;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import lombok.SneakyThrows;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.file.DownloadFileQuery;
import pl.edu.pw.ee.pz.file.FileName;
import pl.edu.pw.ee.pz.file.FilePath;
import pl.edu.pw.ee.pz.file.FileService;
import pl.edu.pw.ee.pz.file.FileSpace;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.util.CustomResourcesInitializer;

@QuarkusTest
@QuarkusTestResource(CustomResourcesInitializer.class)
public class BrandResourceTest {

  @Inject
  FileService fileService;

  @Inject
  PgPool pgPool;

  @Test
  void should_create_brand() {
    // given
    var logoImageBytes = logoImageBytes();

    // when
    var then = RestAssured.given()
        .multiPart("code", "MY_BRAND", MediaType.TEXT_PLAIN)
        .multiPart("logoImage", "MY_BRAND_LOGO.png", logoImageBytes, MediaType.MULTIPART_FORM_DATA)
        .when().post("/brands")
        .then();

    // then9
    then.statusCode(Status.NO_CONTENT.getStatusCode());
    // and: logo file uploaded
    var uploadedLogo = fileService.download(new DownloadFileQuery(
            new FileName("MY_BRAND.logo"),
            new FilePath(new FileSpace("brands"))
        ))
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(uploadedLogo.name().value()).isEqualTo("MY_BRAND.logo");
    assertThat(uploadedLogo.path().space().value()).isEqualTo("brands");
    var uploadedLogoBytes = readBytes(uploadedLogo.content().content());
    assertThat(uploadedLogoBytes).isEqualTo(logoImageBytes);
    // and: projection created
    Awaitility.await()
        .untilAsserted(() -> {
          var brand = pgPool.preparedQuery("""
                  SELECT b.id, b.code FROM brands b WHERE b.code = $1 LIMIT 1
                  """).execute(Tuple.of("MY_BRAND"))
              .onItem().transformToMulti(RowSet::toMulti)
              .onItem().transform(row -> new Brand(
                  new BrandId(UUID.fromString(row.getString("id"))),
                  new BrandCode(row.getString("code"))
              ))
              .collect().first()
              .await().atMost(Duration.ofMillis(500L));

          assertThat(brand.id().value()).isNotBlank();
          assertThat(brand.code().value()).isEqualTo("MY_BRAND");
        });
  }

  private byte[] logoImageBytes() {
    try (var imageLogo = BrandResourceTest.class.getClassLoader().getResourceAsStream("quarkus-favicon.ico")) {
      return requireNonNull(imageLogo).readAllBytes();
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }

  @SneakyThrows
  private byte[] readBytes(InputStream inputStream) {
    try (InputStream is = inputStream) {
      return is.readAllBytes();
    }
  }
}
