package pl.edu.pw.ee.pz.brand;

import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandCode;
import pl.edu.pw.ee.pz.sharedkernel.model.BrandId;

public record ChangeBrandCodeCommand(
    BrandId id,
    BrandCode code
) implements Command {

}
