package pl.edu.pw.ee.pz.brand;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.SneakyThrows;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.brand.port.BrandAggregatePort;
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

  @Inject
  BrandAggregatePort brandAggregatePort;

  @Test
  void should_create_brand() {
    // given
    var logoImageBytes = logoImageBytes();

    // when
    var response = RestAssured.given()
        .multiPart("code", "MY_BRAND", MediaType.TEXT_PLAIN)
        .multiPart("logoImage", "MY_BRAND_LOGO.png", logoImageBytes, MediaType.MULTIPART_FORM_DATA)
        .when().post("/brands");

    // then
    response
        .then()
        .statusCode(Status.CREATED.getStatusCode());
    var responsePayload = response.thenReturn().body().as(CreateBrandResponse.class);
    // and: projection created
    var brand = findBrandProjectionById(responsePayload.id(), Objects::nonNull);
    assertThat(brand.id().value()).isEqualTo(responsePayload.id());
    assertThat(brand.code().value()).isEqualTo("MY_BRAND");
    // and: logo file uploaded
    var logoFileName = "%s.logo".formatted(responsePayload.id());
    var uploadedLogo = fileService.download(new DownloadFileQuery(
            new FileName(logoFileName),
            new FilePath(new FileSpace("brands"))
        ))
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(uploadedLogo.name().value()).isEqualTo(logoFileName);
    assertThat(uploadedLogo.path().space().value()).isEqualTo("brands");
    var uploadedLogoBytes = readBytes(uploadedLogo.content().content());
    assertThat(uploadedLogoBytes).isEqualTo(logoImageBytes);
    // and: brand aggregate is in expected state
    var brandAggregate = brandAggregatePort.findById(new BrandId(UUID.fromString(responsePayload.id())))
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(brandAggregate.id().value()).isEqualTo(responsePayload.id());
    assertThat(brandAggregate.code().value()).isEqualTo("MY_BRAND");
  }

  @Test
  void should_update_brand_code() {
    // given
    var logoImageBytes = logoImageBytes();
    // and: already created brand
    var createBrandResponse = RestAssured.given()
        .multiPart("code", "MY_BRAND", MediaType.TEXT_PLAIN)
        .multiPart("logoImage", "MY_BRAND_LOGO.png", logoImageBytes, MediaType.MULTIPART_FORM_DATA)
        .when().post("/brands")
        .body().as(CreateBrandResponse.class);

    // when
    var then = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body("""
            {"code":"MODIFIED_BRAND"}
            """)
        .when().put("/brands/%s/code".formatted(createBrandResponse.id()))
        .then();

    // then
    then.statusCode(Status.NO_CONTENT.getStatusCode());
    // and: code changed
    var brand = findBrandProjectionById(createBrandResponse.id(), it -> "MODIFIED_BRAND".equals(it.code().value()));
    assertThat(brand.code().value()).isEqualTo("MODIFIED_BRAND");
    // and: brand aggregate is in expected state
    var brandAggregate = brandAggregatePort.findById(new BrandId(UUID.fromString(createBrandResponse.id())))
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(brandAggregate.id().value()).isEqualTo(createBrandResponse.id());
    assertThat(brandAggregate.code().value()).isEqualTo("MODIFIED_BRAND");
  }

  @Test
  void should_fail_updating_brand_code_for_non_existing_brand() {
    // given
    var brandId = UUID.randomUUID().toString();

    // when
    var then = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body("""
            {"code":"MODIFIED_BRAND"}
            """)
        .when().put("/brands/%s/code".formatted(brandId))
        .then();

    // then
    then.statusCode(Status.NOT_FOUND.getStatusCode());
  }

  private Brand findBrandProjectionById(String id, Predicate<Brand> predicate) {
    return Awaitility.await()
        .ignoreExceptions()
        .until(
            () -> pgPool.preparedQuery("""
                    SELECT b.id, b.code FROM brands b WHERE b.id = $1 LIMIT 1
                    """).execute(Tuple.of(id))
                .onItem().transformToMulti(RowSet::toMulti)
                .onItem().transform(row -> new Brand(
                    new BrandId(UUID.fromString(row.getString("id"))),
                    new BrandCode(row.getString("code"))
                ))
                .collect().first()
                .await().atMost(Duration.ofSeconds(1L)),
            predicate
        );
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
