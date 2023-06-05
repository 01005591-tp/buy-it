package pl.edu.pw.ee.pz.product;

final class ProductErrorCode {

  static final String SINGLE_PRODUCT_EXPECTED_BUT_MULTIPLE_FOUND = "SINGLE_PRODUCT_EXPECTED_BUT_MULTIPLE_FOUND";

  private ProductErrorCode() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }
}
