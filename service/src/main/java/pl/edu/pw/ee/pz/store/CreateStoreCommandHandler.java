package pl.edu.pw.ee.pz.store;

import static lombok.AccessLevel.PACKAGE;

import io.smallrye.mutiny.Uni;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler;
import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;
import pl.edu.pw.ee.pz.store.port.StoreAggregatePort;

@RequiredArgsConstructor(access = PACKAGE)
class CreateStoreCommandHandler implements CommandHandler<CreateStoreCommand, StoreId> {

  private final StoreAggregatePort storeAggregatePort;

  @Override
  public Uni<StoreId> handle(CreateStoreCommand command) {
    var storeId = new StoreId(UUID.randomUUID());
    var storeAggregate = new StoreAggregate(storeId, command.code(), command.address());
    return storeAggregatePort.save(storeAggregate)
        .onItem().transform(success -> storeAggregate.id());
  }
}
