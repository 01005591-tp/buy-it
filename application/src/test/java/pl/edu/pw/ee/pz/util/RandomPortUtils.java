package pl.edu.pw.ee.pz.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;

public final class RandomPortUtils {

  private RandomPortUtils() {
    throw new UnsupportedOperationException("Cannot instantiate utility class.");
  }

  public static int findAvailablePort() {
    try (var socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }

}
