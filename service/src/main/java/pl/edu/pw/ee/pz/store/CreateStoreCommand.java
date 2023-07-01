package pl.edu.pw.ee.pz.store;

import pl.edu.pw.ee.pz.sharedkernel.command.Command;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreCode;

public record CreateStoreCommand(
    StoreCode code,
    Address address
) implements Command {

}
