package pl.edu.pw.ee.pz.product;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.With;
import pl.edu.pw.ee.pz.product.ProductFixture.CreateProductSpecification.Attribute;
import pl.edu.pw.ee.pz.product.ProductFixture.CreateProductSpecification.Variation;
import pl.edu.pw.ee.pz.shared.Attribute.AttributeValue;
import pl.edu.pw.ee.pz.sharedkernel.json.JsonSerializer;
import pl.edu.pw.ee.pz.sharedkernel.model.ProductId;

@ApplicationScoped
@RequiredArgsConstructor
public class ProductFixture {

  private final JsonSerializer jsonSerializer;

  public ProductId createProduct() {
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
    return new ProductId(UUID.fromString(createProductResponse.id()));
  }

  public ProductId createProduct(UnaryOperator<CreateProductSpecification> customizer) {
    // given
    var specification = customizer.apply(defaultCreateProductSpecification());
    var requestPayload = jsonSerializer.serializeToBytes(specification);
    // when
    var response = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(requestPayload)
        .when().post("/products")
        .thenReturn();
    // then
    assertThat(response.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
    var createProductResponse = response.body().as(CreateProductResponse.class);
    return new ProductId(UUID.fromString(createProductResponse.id()));
  }

  @SuppressWarnings("unchecked")
  private CreateProductSpecification defaultCreateProductSpecification() {
    return CreateProductSpecification.builder()
        .code("PRODUCT_CODE")
        .brandId(UUID.randomUUID().toString())
        .variation(Variation.builder()
            .attribute(Attribute.builder()
                .type("SIZE_EU")
                .value(AttributeValue.integerAttribute(38))
                .build())
            .attribute(Attribute.builder()
                .type("COLORS")
                .value(AttributeValue.stringAttribute("BROWN_WHITE"))
                .build())
            .build())
        .variation(Variation.builder()
            .attribute(Attribute.builder()
                .type("SIZE_EU")
                .value(AttributeValue.integerAttribute(40))
                .build())
            .attribute(Attribute.builder()
                .type("COLORS")
                .value(AttributeValue.stringAttribute("RED_BLUE"))
                .build())
            .build())
        .build();
  }

  @With
  @Builder(toBuilder = true)
  public record CreateProductSpecification(
      String code,
      String brandId,
      @Singular
      List<Variation> variations
  ) {

    @Builder(toBuilder = true)
    public record Variation(
        // TODO: Must be raw type, because Jackson cannot serialize it properly otherwise
        //       Register custom Jackson serializer to serialize List<Attribute> properly.
        @Singular
        List<Attribute> attributes
    ) {

    }

    @With
    @Builder(toBuilder = true)
    public record Attribute(
        String type,
        pl.edu.pw.ee.pz.shared.Attribute.AttributeValue<?> value
    ) {

    }

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
                "value": {
                  "type": "Integer",
                  "value": "38"
                }
              },
              {
                "type": "COLORS",
                "value": {
                  "type": "String",
                  "value": "BROWN_WHITE"
                }
              }
            ]
          },
          {
            "attributes": [
              {
                "type": "SIZE_EU",
                "value": {
                  "type": "Integer",
                  "value": "40"
                }
              },
              {
                "type": "COLORS",
                "value": {
                  "type": "String",
                  "value": "RED_BLUE"
                }
              }
            ]
          }
        ]
      }
      """;

}
