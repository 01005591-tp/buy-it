package pl.edu.pw.ee.pz.product;

import java.util.List;
import lombok.Data;

@Data
class UpdateProductRequest {

  private String id;
  private String code;
  private String brandCode;
  private List<Variation> variations;
}
