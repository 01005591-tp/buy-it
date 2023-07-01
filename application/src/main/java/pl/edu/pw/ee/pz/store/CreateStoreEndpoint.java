package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import pl.edu.pw.ee.pz.HttpApiError;
import pl.edu.pw.ee.pz.shared.AddressDto;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandlerExecutor;
import pl.edu.pw.ee.pz.sharedkernel.function.UncheckedFunction;
import pl.edu.pw.ee.pz.sharedkernel.model.Address;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.City;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.FlatNo;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.HouseNo;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.Street;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.StreetName;
import pl.edu.pw.ee.pz.sharedkernel.model.Address.ZipCode;
import pl.edu.pw.ee.pz.sharedkernel.model.Country;
import pl.edu.pw.ee.pz.sharedkernel.model.CountryCode;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreCode;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class CreateStoreEndpoint {

  private final CommandHandlerExecutor commandHandlerExecutor;

  Uni<RestResponse<?>> handle(CreateStoreRequest request) {
    var createStoreCommand = toCreateStoreCommand(request);
    return commandHandlerExecutor.execute(createStoreCommand)
        .onItem().transform(UncheckedFunction.from(result -> {
          if (result instanceof StoreId storeId) {
            return RestResponse.status(Status.CREATED, new CreateStoreResponse(storeId.value()));
          } else {
            log.error("Result is not a StoreId instance: {}", result.getClass().getName());
            return RestResponse.status(Status.INTERNAL_SERVER_ERROR, HttpApiError.internalServerError());
          }
        }));
  }

  private CreateStoreCommand toCreateStoreCommand(CreateStoreRequest request) {
    return new CreateStoreCommand(
        new StoreCode(request.code()),
        toAddress(request)
    );
  }

  private Address toAddress(CreateStoreRequest request) {
    return new Address(
        request.address().street().map(this::toStreet),
        request.address().city().map(City::new),
        request.address().zipCode().map(ZipCode::new),
        new Country(CountryCode.of(request.address().country()))
    );
  }

  private Street toStreet(AddressDto.StreetDto streetDto) {
    return new Street(
        new StreetName(streetDto.name()),
        new HouseNo(streetDto.house()),
        streetDto.flatNo().map(FlatNo::new)
    );
  }
}
