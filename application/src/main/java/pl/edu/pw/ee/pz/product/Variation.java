package pl.edu.pw.ee.pz.product;

import java.util.Set;
import lombok.Data;

@Data
class Variation {

  private Set<VariationAttribute> attributes;
}
