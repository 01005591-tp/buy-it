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
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.product.port.ProductAggregatePort;
import pl.edu.pw.ee.pz.sharedkernel.json.JsonSerializer;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.IntegerAttributeValue;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.StringAttributeValue;
import pl.edu.pw.ee.pz.util.CustomResourcesInitializer;

@QuarkusTest
@QuarkusTestResource(CustomResourcesInitializer.class)
class ProductResourceTest {

  @Inject
  ProductProjectionPort productProjectionPort;
  @Inject
  ProductAggregatePort productAggregatePort;
  @Inject
  JsonSerializer jsonSerializer;

  @Test
  void should_create_product() {
    // given
    // when
    var response = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(CREATE_PRODUCT_REQUEST)
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
    assertThat(productAggregate.brand().value()).isEqualTo("b3f141a4-30e7-4b7e-a005-0a969fc8c861");
    assertCreatedProductVariations(productAggregate.variations());
    // and: projection created
    var product = findProductProjectionById(productId, Objects::nonNull);
    assertThat(product.id().value()).isEqualTo(createProductResponse.id());
    assertThat(product.brand().value()).isEqualTo("b3f141a4-30e7-4b7e-a005-0a969fc8c861");
    assertThat(product.code().value()).isEqualTo("MY_PRODUCT");
    assertCreatedProductVariations(product.variations());
  }

  private static void assertCreatedProductVariations(Set<ProductVariation> variations) {
    assertThat(variations)
        .hasSize(2)
        .anySatisfy(variation ->
            assertThat(variation.attributes())
                .hasSize(2)
                .anySatisfy(attribute -> {
                  assertThat(attribute.type().value()).isEqualTo("SIZE_EU");
                  assertThat(attribute.value()).isInstanceOf(IntegerAttributeValue.class);
                  assertThat(attribute.value().value()).isEqualTo(38);
                })
                .anySatisfy(attribute -> {
                  assertThat(attribute.type().value()).isEqualTo("COLORS");
                  assertThat(attribute.value()).isInstanceOf(StringAttributeValue.class);
                  assertThat(attribute.value().value()).isEqualTo("BROWN_WHITE");
                })
        )
        .anySatisfy(variation ->
            assertThat(variation.attributes())
                .hasSize(2)
                .anySatisfy(attribute -> {
                  assertThat(attribute.type().value()).isEqualTo("SIZE_EU");
                  assertThat(attribute.value()).isInstanceOf(IntegerAttributeValue.class);
                  assertThat(attribute.value().value()).isEqualTo(40);
                })
                .anySatisfy(attribute -> {
                  assertThat(attribute.type().value()).isEqualTo("COLORS");
                  assertThat(attribute.value()).isInstanceOf(StringAttributeValue.class);
                  assertThat(attribute.value().value()).isEqualTo("RED_BLUE");
                })
        );
  }

  @Test
  void should_update_product() {
    // given
    var createProductResponse = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(CREATE_PRODUCT_REQUEST)
        .when().post("/products")
        .body().as(CreateProductResponse.class);
    var productId = new ProductId(UUID.fromString(createProductResponse.id()));

    // when
    var response = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(UPDATE_PRODUCT_REQUEST)
        .when().put("/products/%s".formatted(createProductResponse.id()))
        .thenReturn();

    // then
    assertThat(response.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    // and: brand aggregate changed
    var productAggregate = productAggregatePort.findById(productId)
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(productAggregate.id().value()).isEqualTo(createProductResponse.id());
    assertThat(productAggregate.code().value()).isEqualTo("UPDATED_PRODUCT");
    assertThat(productAggregate.brand().value()).isEqualTo("92295a5b-6f75-41c2-87d8-b9669182a20a");
    assertThat(productAggregate.variations())
        .hasSize(1)
        .anySatisfy(
            variation ->
                assertThat(variation.attributes())
                    .hasSize(2)
                    .anySatisfy(attribute -> {
                      assertThat(attribute.type().value()).isEqualTo("SIZE_EU");
                      assertThat(attribute.value()).isInstanceOf(IntegerAttributeValue.class);
                      assertThat(attribute.value().value()).isEqualTo(42);
                    })
                    .anySatisfy(attribute -> {
                      assertThat(attribute.type().value()).isEqualTo("COLORS");
                      assertThat(attribute.value()).isInstanceOf(StringAttributeValue.class);
                      assertThat(attribute.value().value()).isEqualTo("BROWN_ORANGE");
                    })
        );
  }

  private Product findProductProjectionById(ProductId id, Predicate<Product> predicate) {
    return Awaitility.await()
        .ignoreExceptions()
        .until(
            () -> productProjectionPort.findById(id)
                .await().atMost(Duration.ofSeconds(1L)),
            predicate
        );
  }

  private static final String CREATE_PRODUCT_REQUEST = """
      {
        "code": "MY_PRODUCT",
        "brandId": "b3f141a4-30e7-4b7e-a005-0a969fc8c861",
        "variations": [
          {
            "attributes": [
              {
                "type": "SIZE_EU",
                "valueType": "INTEGER",
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
                "valueType": "INTEGER",
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
        "brandId": "92295a5b-6f75-41c2-87d8-b9669182a20a",
        "variations": [
          {
            "attributes": [
              {
                "type": "SIZE_EU",
                "valueType": "INTEGER",
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