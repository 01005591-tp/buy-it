package pl.edu.pw.ee.pz.product;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.product.port.ProductAggregatePort;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.util.CustomResourcesInitializer;

@QuarkusTest
@QuarkusTestResource(CustomResourcesInitializer.class)
class ProductResourceTest {

  @Inject
  ProductAggregatePort productAggregatePort;

  @Test
  void should_create_product() {
    // given
    var brandId = new BrandId(UUID.randomUUID());
    var requestPayload = CREATE_PRODUCT_REQUEST.formatted(brandId.value());

    // when
    var response = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(requestPayload)
        .when().post("/products")
        .thenReturn();

    // then
    assertThat(response.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
    var createProductResponse = response.body().as(CreateProductResponse.class);
    var productId = new ProductId(UUID.fromString(createProductResponse.id()));
    // and: project aggregate is in expected state
    var productAggregate = productAggregatePort.findById(productId)
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(productAggregate.id().value()).isEqualTo(createProductResponse.id());
    assertThat(productAggregate.code().value()).isEqualTo("MY_PRODUCT");
    assertThat(productAggregate.brand()).isEqualTo(brandId);
    assertThat(productAggregate.variations())
        .hasSize(2)
        .anySatisfy(
            variation ->
                assertThat(variation.attributes())
                    .hasSize(2)
                    .anySatisfy(attribute -> {
                      assertThat(attribute.type().value()).isEqualTo("SIZE_EU");
                      assertThat(attribute.value().value()).isEqualTo("38");
                    })
                    .anySatisfy(attribute -> {
                      assertThat(attribute.type().value()).isEqualTo("COLORS");
                      assertThat(attribute.value().value()).isEqualTo("BROWN_WHITE");
                    })
        )
        .anySatisfy(
            variation ->
                assertThat(variation.attributes())
                    .hasSize(2)
                    .anySatisfy(attribute -> {
                      assertThat(attribute.type().value()).isEqualTo("SIZE_EU");
                      assertThat(attribute.value().value()).isEqualTo("40");
                    })
                    .anySatisfy(attribute -> {
                      assertThat(attribute.type().value()).isEqualTo("COLORS");
                      assertThat(attribute.value().value()).isEqualTo("RED_BLUE");
                    })
        );
  }

  @Test
  void should_update_product() {
    // given
    var brandId = new BrandId(UUID.randomUUID());
    var requestPayload = CREATE_PRODUCT_REQUEST.formatted(brandId.value());
    // and
    var createProductResponse = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(requestPayload)
        .when().post("/products")
        .body().as(CreateProductResponse.class);
    var productId = new ProductId(UUID.fromString(createProductResponse.id()));
    // and
    var newBrandId = new BrandId(UUID.randomUUID());
    var updateRequestPayload = UPDATE_PRODUCT_REQUEST.formatted(newBrandId.value());

    // when
    var response = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(updateRequestPayload)
        .when().put("/products/%s".formatted(createProductResponse.id()))
        .thenReturn();

    // then
    assertThat(response.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    // and: brand aggregate changed
    var productAggregate = productAggregatePort.findById(productId)
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(productAggregate.id().value()).isEqualTo(createProductResponse.id());
    assertThat(productAggregate.code().value()).isEqualTo("UPDATED_PRODUCT");
    assertThat(productAggregate.brand()).isEqualTo(newBrandId);
    assertThat(productAggregate.variations())
        .hasSize(1)
        .anySatisfy(
            variation ->
                assertThat(variation.attributes())
                    .hasSize(2)
                    .anySatisfy(attribute -> {
                      assertThat(attribute.type().value()).isEqualTo("SIZE_EU");
                      assertThat(attribute.value().value()).isEqualTo("42");
                    })
                    .anySatisfy(attribute -> {
                      assertThat(attribute.type().value()).isEqualTo("COLORS");
                      assertThat(attribute.value().value()).isEqualTo("BROWN_ORANGE");
                    })
        );
  }

  private static final String CREATE_PRODUCT_REQUEST = """
      {
        "code": "MY_PRODUCT",
        "brandId": "%s",
        "variations": [
          {
            "attributes": [
              {
                "type": "SIZE_EU",
                "value": "38"
              },
              {
                "type": "COLORS",
                "value": "BROWN_WHITE"
              }
            ]
          },
          {
            "attributes": [
              {
                "type": "SIZE_EU",
                "value": "40"
              },
              {
                "type": "COLORS",
                "value": "RED_BLUE"
              }
            ]
          }
        ]
      }
      """;
  private static final String UPDATE_PRODUCT_REQUEST = """
      {
        "code": "UPDATED_PRODUCT",
        "brandId": "%s",
        "variations": [
          {
            "attributes": [
              {
                "type": "SIZE_EU",
                "value": "42"
              },
              {
                "type": "COLORS",
                "value": "BROWN_ORANGE"
              }
            ]
          }
        ]
      }
      """;

}