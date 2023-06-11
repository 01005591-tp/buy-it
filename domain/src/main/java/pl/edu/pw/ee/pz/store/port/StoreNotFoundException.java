package pl.edu.pw.ee.pz.store.port;

import pl.edu.pw.ee.pz.sharedkernel.model.StoreId;

public class StoreNotFoundException extends RuntimeException {

  private StoreNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public static StoreNotFoundException notFound(StoreId id, Throwable cause) {
    return new StoreNotFoundException("Store %s not found".formatted(id.value()), cause);
  }
}
