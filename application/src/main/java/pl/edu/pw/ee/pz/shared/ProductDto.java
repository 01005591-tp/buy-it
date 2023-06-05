package pl.edu.pw.ee.pz.shared;

import java.util.List;

public record ProductDto(
    String id,
    String code,
    String brandId,
    List<Variation> variations
) {


}
