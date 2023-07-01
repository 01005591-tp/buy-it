package pl.edu.pw.ee.pz.store;

import pl.edu.pw.ee.pz.shared.AddressDto;

record CreateStoreRequest(
    String code,
    AddressDto address
) {

}
