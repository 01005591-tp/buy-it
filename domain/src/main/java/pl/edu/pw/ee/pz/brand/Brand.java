package pl.edu.pw.ee.pz.brand;

import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;

public record Brand(
    BrandId id,
    BrandCode code
) {

}
