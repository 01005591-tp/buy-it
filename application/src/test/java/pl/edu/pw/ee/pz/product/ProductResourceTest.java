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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.pz.product.port.ProductAggregatePort;
import pl.edu.pw.ee.pz.shared.Attribute;
import pl.edu.pw.ee.pz.shared.ProductDto;
import pl.edu.pw.ee.pz.shared.Variation;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductVariation;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.IntegerAttributeValue;
import pl.edu.pw.ee.pz.sharedkernel.model.VariationAttribute.StringAttributeValue;
import pl.edu.pw.ee.pz.sharedkernel.query.PageResult.MultiPageResult;
import pl.edu.pw.ee.pz.util.CustomResourcesInitializer;

@QuarkusTest
@QuarkusTestResource(CustomResourcesInitializer.class)
class ProductResourceTest {

  @Inject
  ProductProjectionPort productProjectionPort;
  @Inject
  ProductAggregatePort productAggregatePort;
  @Inject
  ProductFixture productFixture;

  @Test
  void should_create_product() {
    // when
    var productId = productFixture.createProduct();

    // then
    // and: project aggregate is in expected state
    var productAggregate = productAggregatePort.findById(productId)
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(productAggregate.id().value()).isEqualTo(productId.value());
    assertThat(productAggregate.code().value()).isEqualTo("MY_PRODUCT");
    assertThat(productAggregate.brand().value()).isEqualTo("b3f141a4-30e7-4b7e-a005-0a969fc8c861");
    assertCreatedProductVariations(productAggregate.variations());
    // and: projection created
    var product = findProductProjectionById(productId, Objects::nonNull);
    assertThat(product.id().value()).isEqualTo(productId.value());
    assertThat(product.code().value()).isEqualTo("MY_PRODUCT");
    assertThat(product.brand().value()).isEqualTo("b3f141a4-30e7-4b7e-a005-0a969fc8c861");
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
    var productId = productFixture.createProduct();

    // when
    var response = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(UPDATE_PRODUCT_REQUEST)
        .when().put("/products/%s".formatted(productId.value()))
        .thenReturn();

    // then
    assertThat(response.statusCode()).isEqualTo(Status.NO_CONTENT.getStatusCode());
    // and: brand aggregate changed
    var productAggregate = productAggregatePort.findById(productId)
        .await().atMost(Duration.ofSeconds(5L));
    assertThat(productAggregate.id().value()).isEqualTo(productId.value());
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

  @Test
  void should_find_product_by_id() {
    // given
    var productId = productFixture.createProduct();

    Awaitility.await() // await due to eventual consistency in projection creation
        .untilAsserted(() -> {
          // when
          var response = RestAssured.given()
              .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
              .when().get("/products/%s".formatted(productId.value()))
              .thenReturn();

          // then
          assertThat(response.statusCode()).isEqualTo(Status.OK.getStatusCode());
          // and
          var responseBody = response.body().as(SearchProductByIdResponse.class);
          assertThat(responseBody.product().id()).isEqualTo(productId.value());
          assertThat(responseBody.product().code()).isEqualTo("MY_PRODUCT");
          assertThat(responseBody.product().brandId()).isEqualTo("b3f141a4-30e7-4b7e-a005-0a969fc8c861");
          assertCreatedProductVariations(responseBody.product().variations());
        });
  }

  @Test
  void should_find_product_by_managing_criteria_and_criteria_empty() {
    // given
    var brandId = UUID.randomUUID().toString();
    var productId = productFixture.createProduct(spec -> spec.withCode("PRODUCT_1").withBrandId(brandId));
    var brandId2 = UUID.randomUUID().toString();
    var productId2 = productFixture.createProduct(spec -> spec.withCode("PRODUCT_2").withBrandId(brandId2));

    Awaitility.await() // await due to eventual consistency in projection creation
        .untilAsserted(() -> {
          // when
          var response = RestAssured.given()
              .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
              .when().get("/products")
              .thenReturn();

          // then
          assertThat(response.statusCode()).isEqualTo(Status.OK.getStatusCode());
          // and: has expected pagination details
          var responseBody = response.body().as(SearchProductsForManagingResponse.class);
          assertThat(responseBody.products().itemsCount()).isEqualTo(2L);
          assertThat(responseBody.products().pageCount()).isEqualTo(1L);
          assertThat(responseBody.products().isEmpty()).isFalse();
          assertThat(responseBody.products().isSingle()).isFalse();
          assertThat(responseBody.products().isMulti()).isTrue();
          assertThat(responseBody.products().page().size()).isEqualTo(2L);
          assertThat(responseBody.products().page().nextKeySetId()).isEqualTo(2L);
          // and: fetched expected products
          var products = ((MultiPageResult<ProductDto>) responseBody.products()).value();
          assertThat(products)
              .hasSize(2)
              .anySatisfy(product -> {
                assertThat(product.id()).isEqualTo(productId.value());
                assertThat(product.code()).isEqualTo("PRODUCT_1");
                assertThat(product.brandId()).isEqualTo(brandId);
                assertCreatedProductVariations(product.variations());
              })
              .anySatisfy(product -> {
                assertThat(product.id()).isEqualTo(productId2.value());
                assertThat(product.code()).isEqualTo("PRODUCT_2");
                assertThat(product.brandId()).isEqualTo(brandId2);
                assertCreatedProductVariations(product.variations());
              });
        });
  }

  @Test
  void should_find_multiple_pages_product_by_managing_criteria() {
    // given
    IntStream.range(0, 9)
        .forEach(idx -> {
          var brandId = UUID.randomUUID().toString();
          var productCode = "PRODUCT_" + idx;
          productFixture.createProduct(spec -> spec.withCode(productCode).withBrandId(brandId));
        });

    Awaitility.await() // await due to eventual consistency in projection creation
        .untilAsserted(() -> {
          // when
          var response = RestAssured.given()
              .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
              .queryParam("pageSize", 4)
              .when().get("/products")
              .thenReturn();

          // then
          assertThat(response.statusCode()).isEqualTo(Status.OK.getStatusCode());
          // and: has expected pagination details
          var responseBody = response.body().as(SearchProductsForManagingResponse.class);
          assertThat(responseBody.products().itemsCount()).isEqualTo(9L);
          assertThat(responseBody.products().pageCount()).isEqualTo(3L);
          assertThat(responseBody.products().isEmpty()).isFalse();
          assertThat(responseBody.products().isSingle()).isFalse();
          assertThat(responseBody.products().isMulti()).isTrue();
          assertThat(responseBody.products().page().size()).isEqualTo(4L);
          assertThat(responseBody.products().page().nextKeySetId()).isEqualTo(4L);
        });
  }

  private static void assertCreatedProductVariations(List<Variation> variations) {
    assertThat(variations)
        .hasSize(2)
        .anySatisfy(variation ->
            assertThat(variation.attributes())
                .hasSize(2)
                .anySatisfy(attribute -> {
                  assertThat(attribute.type()).isEqualTo("SIZE_EU");
                  assertThat(attribute.value()).isInstanceOf(Attribute.IntegerAttributeValue.class);
                  assertThat(attribute.value().value()).isEqualTo(38);
                })
                .anySatisfy(attribute -> {
                  assertThat(attribute.type()).isEqualTo("COLORS");
                  assertThat(attribute.value()).isInstanceOf(Attribute.StringAttributeValue.class);
                  assertThat(attribute.value().value()).isEqualTo("BROWN_WHITE");
                })
        )
        .anySatisfy(variation ->
            assertThat(variation.attributes())
                .hasSize(2)
                .anySatisfy(attribute -> {
                  assertThat(attribute.type()).isEqualTo("SIZE_EU");
                  assertThat(attribute.value()).isInstanceOf(Attribute.IntegerAttributeValue.class);
                  assertThat(attribute.value().value()).isEqualTo(40);
                })
                .anySatisfy(attribute -> {
                  assertThat(attribute.type()).isEqualTo("COLORS");
                  assertThat(attribute.value()).isInstanceOf(Attribute.StringAttributeValue.class);
                  assertThat(attribute.value().value()).isEqualTo("RED_BLUE");
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

  private static final String UPDATE_PRODUCT_REQUEST = """
      {
        "code": "UPDATED_PRODUCT",
        "brandId": "92295a5b-6f75-41c2-87d8-b9669182a20a",
        "variations": [
          {
            "attributes": [
              {
                "type": "SIZE_EU",
                "value": {
                  "type": "Integer",
                  "value": "42"
                }
              },
              {
                "type": "COLORS",
                "value": {
                  "type": "String",
                  "value": "BROWN_ORANGE"
                }
              }
            ]
          }
        ]
      }
      """;

}