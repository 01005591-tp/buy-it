package pl.edu.pw.ee.pz.store;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.vavr.control.Option;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.With;
import pl.edu.pw.ee.pz.shared.AddressDto;
import pl.edu.pw.ee.pz.shared.AddressDto.StreetDto;
import pl.edu.pw.ee.pz.sharedkernel.json.JsonSerializer;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

@ApplicationScoped
@RequiredArgsConstructor
public class StoreFixture {

  private final JsonSerializer jsonSerializer;

  public StoreId createStore() {
    return createStore(UnaryOperator.identity());
  }

  public StoreId createStore(UnaryOperator<CreateStoreSpecification> customizer) {
    var specification = customizer.apply(defaultCreateStoreSpecification());
    var response = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(specification.toCreateStoreRequest())
        .when().post("/stores")
        .thenReturn();

    assertThat(response.statusCode()).isEqualTo(Status.CREATED.getStatusCode());
    var createStoreResponse = response.body().as(CreateStoreResponse.class);
    return new StoreId(UUID.fromString(createStoreResponse.id()));
  }

  public void updateStoreProductAvailability(StoreId storeId) {
    updateStoreProductAvailability(storeId, UnaryOperator.identity());
  }

  public void updateStoreProductAvailability(
      StoreId storeId,
      UnaryOperator<UpdateProductAvailabilitySpecification> customizer
  ) {
    var specification = customizer.apply(defaultUpdateProductAvailabilitySpecification());
    var requestPayload = jsonSerializer.serializeToBytes(specification);
    var response = RestAssured.given()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .body(requestPayload)
        .when().put("stores/%s/products/pieces".formatted(storeId.value()))
        .thenReturn();

    assertThat(response.getStatusCode()).isEqualTo(Status.OK.getStatusCode());
  }

  private CreateStoreSpecification defaultCreateStoreSpecification() {
    return CreateStoreSpecification.builder()
        .storeId(UUID.randomUUID().toString())
        .code("STORE_1")
        .address(AddressSpecification.builder()
            .street(StreetSpecification.builder()
                .name("Street")
                .houseNo("123C")
                .flatNo("5")
                .build())
            .city("City")
            .zipCode("01234")
            .country("PL")
            .build())
        .build();
  }

  private UpdateProductAvailabilitySpecification defaultUpdateProductAvailabilitySpecification() {
    return UpdateProductAvailabilitySpecification.builder()
        .product(UpdateProductSpecification.builder()
            .productId(UUID.randomUUID().toString())
            .variationPiece(VariationPiecesSpecification.builder()
                .variationId(UUID.randomUUID().toString())
                .pieces(3L)
                .build())
            .variationPiece(VariationPiecesSpecification.builder()
                .variationId(UUID.randomUUID().toString())
                .pieces(5L)
                .build())
            .variationPiece(VariationPiecesSpecification.builder()
                .variationId(UUID.randomUUID().toString())
                .pieces(7L)
                .build())
            .build())
        .product(UpdateProductSpecification.builder()
            .productId(UUID.randomUUID().toString())
            .variationPiece(VariationPiecesSpecification.builder()
                .variationId(UUID.randomUUID().toString())
                .pieces(2L)
                .build())
            .variationPiece(VariationPiecesSpecification.builder()
                .variationId(UUID.randomUUID().toString())
                .pieces(4L)
                .build())
            .build())
        .build();
  }

  @With
  @Builder(toBuilder = true)
  public record CreateStoreSpecification(
      String storeId,
      String code,
      AddressSpecification address
  ) {

    public CreateStoreRequest toCreateStoreRequest() {
      return new CreateStoreRequest(
          code,
          address.toAddressDto()
      );
    }

  }

  @With
  @Builder(toBuilder = true)
  public record AddressSpecification(
      StreetSpecification street,
      String city,
      String zipCode,
      String country
  ) {

    public AddressDto toAddressDto() {
      return new AddressDto(
          Option.of(street).map(StreetSpecification::toStreetDto).filter(Objects::nonNull),
          Option.of(city),
          Option.of(zipCode),
          country
      );
    }
  }

  @With
  @Builder(toBuilder = true)
  public record StreetSpecification(
      String name,
      String houseNo,
      String flatNo
  ) {

    public StreetDto toStreetDto() {
      return new StreetDto(
          name,
          houseNo,
          Option.of(flatNo)
      );
    }
  }

  @With
  @Builder(toBuilder = true)
  public record UpdateProductAvailabilitySpecification(
      @Singular
      List<UpdateProductSpecification> products
  ) {

  }

  @With
  @Builder(toBuilder = true)
  public record UpdateProductSpecification(
      String productId,
      @Singular
      List<VariationPiecesSpecification> variationPieces
  ) {

  }

  @With
  @Builder(toBuilder = true)
  public record VariationPiecesSpecification(
      String variationId,
      Long pieces
  ) {

  }
}
