package pl.edu.pw.ee.pz.brand;

import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandLogo;

public record NewBrandCommand(
    BrandCode code,
    BrandLogo logo
) implements Command {

}
