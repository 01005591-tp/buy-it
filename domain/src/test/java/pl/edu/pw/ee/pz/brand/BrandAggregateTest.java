package pl.edu.pw.ee.pz.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.edu.pw.ee.pz.brand.error.InvalidBrandCodeException;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;

class BrandAggregateTest {

  @Test
  void should_create_new_brand() {
    // given
    var brandId = new BrandId(UUID.randomUUID());
    var brandCode = new BrandCode("TEST_BRAND");

    // when
    var brandAggregate = new BrandAggregate(brandId, brandCode);

    // then
    assertThat(brandAggregate.id()).isEqualTo(brandId);
    assertThat(brandAggregate.code()).isEqualTo(brandCode);
  }

  @Test
  void should_change_brand_code() {
    // given
    var brandAggregate = newBrand();
    var newCode = new BrandCode("NEW_TEST_BRAND");

    // when
    brandAggregate.changeCode(newCode);

    // then
    assertThat(brandAggregate.code()).isEqualTo(newCode);
  }


  @CsvSource(nullValues = "null", value = {
      " ''   ",
      " ' '  ",
      " '\n' ",
      " null ",
  })
  @ParameterizedTest(name = "[{index}] newCode = {0}")
  void should_fail_changing_code_when_given_empty_code(String code) {
    // given
    var brandAggregate = newBrand();
    var newCode = new BrandCode(code);

    // when
    var throwableAssert = assertThatCode(() -> brandAggregate.changeCode(newCode));

    // then
    throwableAssert
        .isInstanceOf(InvalidBrandCodeException.class)
        .hasMessage("Brand code cannot be null, blank or empty. Received %s".formatted(code));
  }

  @Test
  void should_fail_changing_code_when_given_the_same_code() {
    // given
    var brandAggregate = newBrand();

    // when
    var throwableAssert = assertThatCode(() -> brandAggregate.changeCode(brandAggregate.code()));

    // then
    throwableAssert
        .isInstanceOf(InvalidBrandCodeException.class)
        .hasMessage("Brand code already has the expected code: %s".formatted(brandAggregate.code().value()));
  }

  private BrandAggregate newBrand() {
    return new BrandAggregate(new BrandId(UUID.randomUUID()), new BrandCode("TEST_BRAND"));
  }
}